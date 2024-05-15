package com.example.bts.fees;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bts.R;

public class FeePayment extends AppCompatActivity {

    String userRole;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feepay);

        // Retrieve `userRole` and `userId` from the intent that started this activity
        Intent intent = getIntent();
        userRole = intent.getStringExtra("userRole");
        userId = intent.getStringExtra("userId");

        // Initialize the spinner
        Spinner spinner = findViewById(R.id.FeeMethods);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_methods, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Get reference to the "Pay" button
        Button payButton = findViewById(R.id.SubmitFee);

        // Set click listener for the "Pay" button
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action to redirect to EasyPaisa
                redirectToJazzCash();
            }
        });
    }

    // Method to redirect to EasyPaisa app
    private void redirectToJazzCash() {
        String jazzCashPackageName = "com.jazzcash.jazzcash";
        String jazzCashUrl = "https://www.jazzcash.com.pk/";

        Intent jazzCashIntent = getPackageManager().getLaunchIntentForPackage(jazzCashPackageName);
        if (jazzCashIntent != null) {
            // JazzCash app is installed, so open it
            startActivity(jazzCashIntent);
        } else {
            // JazzCash app is not installed, open JazzCash website
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jazzCashUrl));
            startActivity(webIntent);
        }
    }
}
