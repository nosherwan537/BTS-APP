package com.example.bts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bts.Authentication.DriverLoginActivity;
import com.example.bts.Authentication.MainActivity;
import com.example.bts.ChatModule.ProfileFragment;
import com.example.bts.ChatModule.chatActivity;
import com.example.bts.feedback.Feedback;
import com.example.bts.fees.FeePayment;

public class HomePageActivity extends AppCompatActivity {

    private String userRole;
    private String userId;
    private FrameLayout fragmentContainer;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Retrieve `userRole` and `userId` from the intent that started this activity
        Intent intent = getIntent();
        userRole = intent.getStringExtra("userRole");
        userId = intent.getStringExtra("userId");

        // Retrieve references to the ImageView elements
        ImageView chatButton = findViewById(R.id.imageView14);
        ImageView mapButton = findViewById(R.id.imageView10);
        ImageView feedbackButton = findViewById(R.id.imageView15);
        ImageView feeButton = findViewById(R.id.imageView11);
        ImageView profileButton = findViewById(R.id.imageView12);
        Button pickupButton = findViewById(R.id.pickup);

        // Initialize fragmentContainer
        fragmentContainer = findViewById(R.id.fragment_container);
        scrollView = findViewById(R.id.scrollView2);

        // Show or hide the pickup button based on the user role
        if ("driver".equals(userRole)) {
            pickupButton.setVisibility(View.GONE);
        } else {
            pickupButton.setVisibility(View.VISIBLE);
            pickupButton.setOnClickListener(v -> {
                // Start `UserLocationActivity` when the pickup button is clicked
                Intent userLocationIntent = new Intent(HomePageActivity.this, UserLocationActivity.class);
                // Pass `userId` to `UserLocationActivity`
                userLocationIntent.putExtra("userId", userId);
                startActivity(userLocationIntent);
            });
        }

        // Set onClickListener for the chat button
        chatButton.setOnClickListener(v -> {
            // Start `chatActivity` when the chat button is clicked
            Intent chatIntent = new Intent(HomePageActivity.this, chatActivity.class);
            // Pass userRole and userId to chatActivity (if required)
            chatIntent.putExtra("userRole", userRole);
            chatIntent.putExtra("userId", userId);
            startActivity(chatIntent);
        });

        // Set onClickListener for the fee button
        feeButton.setOnClickListener(v -> {
            // Start `FeePayment` when the fee button is clicked
            Intent feeIntent = new Intent(HomePageActivity.this, FeePayment.class);
            // Pass `userRole` and `userId` to `FeePayment`
            feeIntent.putExtra("userRole", userRole);
            feeIntent.putExtra("userId", userId);
            startActivity(feeIntent);
        });

        // Set onClickListener for the map button
        mapButton.setOnClickListener(v -> {
            // Start `MapsActivity` when the map button is clicked
            Intent mapIntent = new Intent(HomePageActivity.this, MapsActivity.class);
            // Pass `userRole` and `userId` to `MapsActivity`
            mapIntent.putExtra("userRole", userRole);
            mapIntent.putExtra("userId", userId);
            startActivity(mapIntent);
        });

        // Set onClickListener for the feedback button
        feedbackButton.setOnClickListener(v -> {
            // Start `Feedback` when the feedback button is clicked
            Intent feedbackIntent = new Intent(HomePageActivity.this, Feedback.class);
            // Pass `userRole` and `userId` to `Feedback`
            feedbackIntent.putExtra("userRole", userRole);
            feedbackIntent.putExtra("userId", userId);
            startActivity(feedbackIntent);
        });

        // Set onClickListener for the profile button
        profileButton.setOnClickListener(v -> {
            // Load `ProfileFragment` when the profile button is clicked
            loadProfileFragment();
        });
    }

    private void loadProfileFragment() {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("userRole", userRole);
        args.putString("userId", userId);
        profileFragment.setArguments(args);

        fragmentContainer.setVisibility(View.VISIBLE); // Make fragment container visible

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, profileFragment);
        scrollView.setVisibility(View.GONE);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        // Check if any fragment is currently visible
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // If a fragment is visible, pop it from the back stack
            getSupportFragmentManager().popBackStack();
            fragmentContainer.setVisibility(View.GONE); // Hide fragment container
            scrollView.setVisibility(View.VISIBLE); // Show the main content
        } else {
            // If no fragment is visible, check the user role
            if ("driver".equals(userRole)) {
                // Start `DriverHomePageActivity` for drivers
                Intent driverHomeIntent = new Intent(HomePageActivity.this, DriverLoginActivity.class);
                driverHomeIntent.putExtra("userId", userId);
                startActivity(driverHomeIntent);
                finish();
            } else {
                // Start `LoginActivity` for other users
                Intent loginIntent = new Intent(HomePageActivity.this, MainActivity.class);
                loginIntent.putExtra("userId", userId);
                startActivity(loginIntent);
                finish();
            }
        }
    }
}