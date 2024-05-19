package com.example.bts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bts.ChatModule.chatActivity;
import com.example.bts.feedback.Feedback;
import com.example.bts.fees.FeePayment;

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
        Button feedback_button = findViewById(R.id.feedback);
        Button fee_button = findViewById(R.id.Fees);

        // Set onClickListener for the chat button
        chatButton.setOnClickListener(v -> {
            // Start `chatActivity` when the chat button is clicked
            Intent chatIntent = new Intent(HomePageActivity.this, chatActivity.class);
            // Pass userRole and userId to chatActivity (if required)
            chatIntent.putExtra("userRole", userRole);
            chatIntent.putExtra("userId", userId);
            startActivity(chatIntent);
        });
        fee_button.setOnClickListener(v -> {
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

        feedback_button.setOnClickListener(v -> {
            // Start `Feedback` when the feedback button is clicked
            Intent feedbackIntent = new Intent(HomePageActivity.this, Feedback.class);
            // Pass `userRole` and `userId` to `Feedback`
            feedbackIntent.putExtra("userRole", userRole);
            feedbackIntent.putExtra("userId", userId);
            startActivity(feedbackIntent);
        });
    }
}
