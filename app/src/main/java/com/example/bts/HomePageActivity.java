package com.example.bts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {

    private String userRole;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Retrieve `userRole` and `userId` from the intent that started this activity
        Intent intent = getIntent();
        userRole = intent.getStringExtra("userRole");
        userId = intent.getStringExtra("userId");

        // Retrieve references to the chat and map buttons
        Button chatButton = findViewById(R.id.chat_button);
        Button mapButton = findViewById(R.id.map_button);

        // Set onClickListener for the chat button
        chatButton.setOnClickListener(v -> {
            // Start `chatActivity` when the chat button is clicked
            Intent chatIntent = new Intent(HomePageActivity.this, chatActivity.class);
            // Pass userRole and userId to chatActivity (if required)
            chatIntent.putExtra("userRole", userRole);
            chatIntent.putExtra("userId", userId);
            startActivity(chatIntent);
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
    }
}
