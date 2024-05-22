package com.example.bts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.bts.model.LocationData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private String userId;
    private Button selectPickupButton;
    private Button selectCurrentLocationButton;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);

        // Retrieve userId from the Intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize the buttons
        selectPickupButton = findViewById(R.id.select_pickup_button);
        selectCurrentLocationButton = findViewById(R.id.current_location_button);

        selectPickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableMapClick();
            }
        });

        selectCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set a map click listener to get the location selected by the user
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                saveLocationToFirestore(latLng);
            }
        });

        // Initially disable map clicks
        mMap.setOnMapClickListener(null);
    }

    private void enableMapClick() {
        // Enable the map click listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                saveLocationToFirestore(latLng);
            }
        });
        Toast.makeText(this, "Tap on the map to select your pickup location", Toast.LENGTH_SHORT).show();
    }

    private void getCurrentLocation() {
        // Check for permission to access the device's location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }

        // Get the fused location provider client
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the last known location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Create a LatLng object from the location
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                            // Save the current location to Firestore
                            saveLocationToFirestore(currentLatLng);
                        } else {
                            Toast.makeText(UserLocationActivity.this, "Failed to get current location. Make sure location services are enabled.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveLocationToFirestore(LatLng latLng) {
        // Create a LocationData object
        LocationData locationData = new LocationData(latLng.latitude, latLng.longitude);

        // Update the location data inside the user's document
        db.collection("users").document(userId)
                .update("location", locationData)
                .addOnSuccessListener(aVoid -> {
                    mMap.clear(); // Clear previous markers
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    Toast.makeText(UserLocationActivity.this, "Location saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(UserLocationActivity.this, "Failed to save location", Toast.LENGTH_SHORT).show());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get current location
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied. Unable to get current location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
