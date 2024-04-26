package com.example.bts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bts.R;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Retrieve reference to the chat button after setContentView
        Button chatButton = findViewById(R.id.chat_button);
        Button mapButton = findViewById(R.id.map_button);

        // Set onClickListener for the chat button
        chatButton.setOnClickListener(v -> {
            // Start MainActivity when the chat button is clicked
            Intent intent = new Intent(HomePageActivity.this, chatActivity.class);
            startActivity(intent);
        });
        mapButton.setOnClickListener(v -> {
            // Start MapsActivity when the map button is clicked
            Intent intent = new Intent(HomePageActivity.this, MapsActivity.class);
            startActivity(intent);
        });
    }
}



