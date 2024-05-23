package com.example.bts.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bts.R;

public  class Role extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.role);

        // Initialize the Spinner
        Spinner dropdownSpinner = findViewById(R.id.spinner);
        Button choiceButton = findViewById(R.id.button);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.driver_user_choices,
                android.R.layout.simple_spinner_item
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the Spinner
        dropdownSpinner.setAdapter(adapter);

        // Set an OnItemSelectedListener to handle the user's selection (optional)
        dropdownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Optional: Handle item selection changes if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no choice is selected (if needed)
            }
        });

        // Set an OnClickListener for the button
        choiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve the selected choice from the Spinner
                String selectedChoice = (String) dropdownSpinner.getSelectedItem();

                // Navigate to the appropriate login activity based on the selected choice
                if (selectedChoice.equals("Driver")) {
                    // Start the DriverLoginActivity
                    Intent intent = new Intent(Role.this, DriverLoginActivity.class);
                    startActivity(intent);
                } else if (selectedChoice.equals("User")) {
                    // Start the UserLoginActivity (MainActivity)
                    Intent intent = new Intent(Role.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
