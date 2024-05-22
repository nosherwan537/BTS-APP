package com.example.bts.ChatModule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bts.HomePageActivity;
import com.example.bts.R;
import com.example.bts.feedback.Feedback;
import com.example.bts.fees.FeePayment;
import com.example.bts.utils.FirebaseUtil;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

public class chatActivity extends AppCompatActivity {

    ImageButton searchButton;

    ChatFragment chatFragment;
    String userRole;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        userRole = intent.getStringExtra("userRole");
        userId = intent.getStringExtra("userId");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        chatFragment = new ChatFragment();
        searchButton = findViewById(R.id.main_search_btn);

        searchButton.setOnClickListener((v) -> {
            startActivity(new Intent(chatActivity.this, SearchUserActivity.class));
        });

        setupBottomAppBar(userRole, userId);

        if (userRole.equals("driver")) {
            getDriverFCMToken();
        } else {
            getUserFCMToken();
        }

        // Initially display the chat fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("userRole", userRole);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }



    private void setupBottomAppBar(String userRole, String userId) {
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        LinearLayout feeLayout = (LinearLayout) linearLayout.findViewById(R.id.imageView11).getParent();
        LinearLayout chatLayout = (LinearLayout) linearLayout.findViewById(R.id.imageView14).getParent();
        LinearLayout feedbackLayout = (LinearLayout) linearLayout.findViewById(R.id.imageView15).getParent();

        feeLayout.setOnClickListener(v -> {
            // Handle fee layout click
            Intent intent = new Intent(chatActivity.this, FeePayment.class);
            intent.putExtra("userRole", userRole);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
        chatLayout.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
        });

        feedbackLayout.setOnClickListener(v -> {
            // Handle feedback layout click
            Intent intent = new Intent(chatActivity.this, Feedback.class);
            intent.putExtra("userRole", userRole);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    void getUserFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                FirebaseUtil.currentUserDetails().update("fcmToken", token);
            }
        });
    }

    void getDriverFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                FirebaseUtil.currentDriverDetails().update("fcmToken", token);
            }
        });
    }
}
