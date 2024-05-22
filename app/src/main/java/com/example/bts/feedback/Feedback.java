package com.example.bts.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bts.ChatModule.chatActivity;
import com.example.bts.HomePageActivity;
import com.example.bts.R;
import com.example.bts.fees.FeePayment;
import com.example.bts.model.FeedBackModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Feedback extends AppCompatActivity {

    private EditText feedbackEditText;
    private Button submitButton;
    private String userId;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        Intent intent = getIntent();
        userRole = intent.getStringExtra("userRole");
        userId = intent.getStringExtra("userId");

        feedbackEditText = findViewById(R.id.editText);
        submitButton = findViewById(R.id.feedback_button);
        setupBottomAppBar(userRole, userId);

        submitButton.setOnClickListener(view -> submitFeedback());
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

        feeLayout.setOnClickListener(v -> {
            // Handle fee layout click
            Intent intent = new Intent(Feedback.this, FeePayment.class);
            intent.putExtra("userRole", userRole);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
        chatLayout.setOnClickListener(v -> {
            // Handle chat layout click
            Intent intent = new Intent(Feedback.this, chatActivity.class);
            intent.putExtra("userRole", userRole);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

    }

    private void submitFeedback() {
        String feedbackText = feedbackEditText.getText().toString().trim();
        if (!feedbackText.isEmpty()) {
            // Get current user ID (Assuming you have user authentication implemented)
            String userId = getCurrentUserId();

            if (userId != null) {
                // Create a new Feedback object
                FeedBackModel feedback = new FeedBackModel();
                feedback.setUserId(userId);
                feedback.setFeedbackText(feedbackText);
                feedback.setTimestamp(System.currentTimeMillis());

                // Store the feedback in Firestore
                addFeedbackToFirestore(feedback);
            } else {
                showToast("User not authenticated. Please log in.");
            }
        } else {
            showToast("Feedback cannot be empty.");
        }
    }

    private String getCurrentUserId() {
        return userId;
    }
    private String getCurrentUserRole() {
        return userRole;
    }

    private void addFeedbackToFirestore(FeedBackModel feedback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference feedbackRef = db.collection("feedback");

        feedbackRef.add(feedback)
                .addOnSuccessListener(documentReference -> {
                    showToast("Feedback submitted successfully!");
                    finish();
                })
                .addOnFailureListener(e -> {
                    showToast("Failed to submit feedback. Please try again later.");
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
