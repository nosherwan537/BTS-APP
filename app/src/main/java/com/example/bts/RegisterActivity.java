package com.example.bts;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class RegisterActivity extends AppCompatActivity {

    private RegisterApi registerApi;
    private EditText editTextName, editTextEmail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register); // Use activity_register.xml instead of activity_main.xml

        // Initialize RegisterApi instance
        registerApi = new RegisterApi(this);

        // Initialize EditText fields
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        // Register Button click listener
        Button registerButton = findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input from EditText fields
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Call registerUser method with user input
                registerUser(name, email, password);
            }
        });
    }


    private void registerUser(String name, String email, String password) {
        // Call registerUser method from RegisterApi class
        registerApi.registerUser(name, email, password, new RegisterApi.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    // Handle successful registration response
                    String message = response.getString("message");
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    // Clear EditText fields after successful registration
                    editTextName.setText("");
                    editTextEmail.setText("");
                    editTextPassword.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                // Log the full error message
                Log.e("Volley Error", "Registration failed with error: " + error.getMessage(), error);

                // Handle registration error
                String errorMessage = "Registration failed. Please try again.";
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        JSONObject jsonObject = new JSONObject(responseBody);
                        errorMessage = jsonObject.getString("message");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}