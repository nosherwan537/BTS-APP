package com.example.bts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 101;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference userRef;
    private DatabaseReference driverRef;
    private LocationCallback locationCallback;

    // Assume userRole is either "driver" or "user" based on the signup or login process
    private String userRole;
    private String userId; // Assume you have the userId available

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        // Retrieve userRole and userId from the Intent
        Intent intent = getIntent();
        userRole = intent.getStringExtra("userRole");
        userId = intent.getStringExtra("userId");
        Log.d("MapsActivity", "userRole: " + userRole);
        Log.d("MapsActivity", "userId: " + userId);

        // Initialize Firebase Database references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users");
        driverRef = database.getReference("drivers");

        // Initialize Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Handle the location update
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    updateMapAndDatabase(currentLocation);
                }
            }
        };

        // Request location permissions
        requestLocationPermissions();
    }


    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            // Permission already granted
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        // Check if location permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Create a LocationRequest using the builder
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10 * 1000) // 10 seconds in milliseconds
                    .setMinUpdateIntervalMillis(5 * 1000) // 5 seconds in milliseconds
                    .build();

            // Initialize the FusedLocationProviderClient
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            // Request location updates
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            // Request location permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }
    }


    // Method to update map and store the location in Firebase
    private void updateMapAndDatabase(LatLng currentLocation) {
        mMap.clear(); // Clear previous markers

        // Set marker based on user role
        String title;
        if (userRole.equals("driver")) {
            title = "Driver's Location";
            driverRef.child(userId).child("location").setValue(currentLocation);
        } else {
            title = "User's Location";
            userRef.child(userId).child("location").setValue(currentLocation);
        }

        // Add marker to the map
        mMap.addMarker(new MarkerOptions().position(currentLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }

    // Fetch and display each other's location
    private void fetchAndDisplayLocations() {
        if (userRole.equals("driver")) {
            // If the user is a driver, listen for the user's (passenger's) location
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LatLng userLocation = snapshot.child("location").getValue(LatLng.class);
                        if (userLocation != null) {
                            updateUserLocationOnMap(userLocation);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MapsActivity.this, "Failed to fetch user location", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If the user is a passenger, listen for the driver's location
            driverRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LatLng driverLocation = snapshot.child("location").getValue(LatLng.class);
                        if (driverLocation != null) {
                            updateDriverLocationOnMap(driverLocation);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MapsActivity.this, "Failed to fetch driver location", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void updateUserLocationOnMap(LatLng userLocation) {
        mMap.addMarker(new MarkerOptions().position(userLocation)
                .title("Passenger's Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }

    private void updateDriverLocationOnMap(LatLng driverLocation) {
        mMap.addMarker(new MarkerOptions().position(driverLocation)
                .title("Driver's Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enable location layer if permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Fetch and display each other's location
        fetchAndDisplayLocations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove location updates when the activity is destroyed
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    // Handle permissions request response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                startLocationUpdates();
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
