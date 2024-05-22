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

import com.example.bts.model.LocationData;
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

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 101;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference userRef;
    private DatabaseReference driverRef;
    private LocationCallback locationCallback;

    private String userRole;
    private String userId;
    private List<String> assignedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        Intent intent = getIntent();
        userRole = intent.getStringExtra("userRole");
        userId = intent.getStringExtra("userId");
        Log.d("MapsActivity", "userRole: " + userRole);
        Log.d("MapsActivity", "userId: " + userId);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users");
        driverRef = database.getReference("drivers");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    updateMapAndDatabase(currentLocation);
                }
            }
        };

        requestLocationPermissions();
    }

    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10 * 1000)
                    .setMinUpdateIntervalMillis(5 * 1000)
                    .build();

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }
    }

    private void updateMapAndDatabase(LatLng currentLocation) {
        mMap.clear();

        LocationData locationData = new LocationData(currentLocation.latitude, currentLocation.longitude);

        if (userRole.equals("driver")) {
            driverRef.child(userId).child("location").setValue(locationData);
        } else {
            userRef.child(userId).child("location").setValue(locationData);
        }

        String title = userRole.equals("driver") ? "Driver's Location" : "User's Location";
        mMap.addMarker(new MarkerOptions().position(currentLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }

    private void fetchAndDisplayLocations() {
        if (userRole.equals("driver")) {
            driverRef.child(userId).child("assignedUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        assignedUsers = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            assignedUsers.add(snapshot.getValue(String.class));
                        }
                        Log.d("MapsActivity", "Assigned users: " + assignedUsers.toString());

                        for (String userId : assignedUsers) {
                            userRef.child(userId).child("location").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    LocationData userLocationData = snapshot.getValue(LocationData.class);
                                    if (userLocationData != null) {
                                        LatLng userLocation = new LatLng(userLocationData.getLatitude(), userLocationData.getLongitude());
                                        updateUserLocationOnMap(userLocation);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(MapsActivity.this, "Failed to fetch user location", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Log.d("MapsActivity", "No assigned users found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MapsActivity.this, "Failed to fetch assigned users", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            driverRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LocationData driverLocationData = snapshot.child("location").getValue(LocationData.class);
                        if (driverLocationData != null) {
                            LatLng driverLocation = new LatLng(driverLocationData.getLatitude(), driverLocationData.getLongitude());
                            updateDriverLocationOnMap(driverLocation);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        fetchAndDisplayLocations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
