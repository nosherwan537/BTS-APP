package com.example.bts.fees;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bts.ChatModule.chatActivity;
import com.example.bts.HomePageActivity;
import com.example.bts.R;
import com.example.bts.feedback.Feedback;

public class FeePayment extends AppCompatActivity {

    String userRole;
    String userId;
    String selectedPaymentMethod;

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

        // Set item selected listener for the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPaymentMethod = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where no item is selected if necessary
            }
        });

        // Get reference to the "Pay" button
        Button payButton = findViewById(R.id.SubmitFee);

        // Set click listener for the "Pay" button
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action based on the selected payment method
                if (selectedPaymentMethod.equals("EasyPaisa")) {
                    redirectToEasyPaisa();
                } else if (selectedPaymentMethod.equals("JazzCash")) {
                    redirectToJazzCash();
                } else if (selectedPaymentMethod.equals("PayPro")) {
                    redirectToPayPro();
                } else {
                    Toast.makeText(FeePayment.this, "Please select a valid payment method", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setupBottomAppBar(userRole, userId);
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
        LinearLayout feedbackLayout = (LinearLayout) linearLayout.findViewById(R.id.imageView15).getParent();
        LinearLayout chatLayout = (LinearLayout) linearLayout.findViewById(R.id.imageView14).getParent();
        chatLayout.setOnClickListener(v -> {
            // Handle chat layout click
            Intent intent = new Intent(FeePayment.this, chatActivity.class);
            intent.putExtra("userRole", userRole);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
        feedbackLayout.setOnClickListener(v -> {
            // Handle feedback layout click
            Intent intent = new Intent(FeePayment.this, Feedback.class);
            intent.putExtra("userRole", userRole);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

    }


    // Method to redirect to EasyPaisa app
    private void redirectToEasyPaisa() {
        String easyPaisaUrl = "https://easypaisa.com.pk/";
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(easyPaisaUrl));
        startActivity(webIntent);
    }

    // Method to redirect to JazzCash app
    private void redirectToJazzCash() {
        String jazzCashUrl = "https://www.jazzcash.com.pk/";
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jazzCashUrl));
        startActivity(webIntent);
    }

    // Method to redirect to PayPro app
    private void redirectToPayPro() {
        String payProUrl = "https://paypro.com.pk/e-commerce/?utm_term=&utm_campaign=Generate-Traffic-Campaign&utm_source=adwords&utm_medium=ppc&hsa_acc=4895178007&hsa_cam=12349262536&hsa_grp=127582426696&hsa_ad=545930476727&hsa_src=g&hsa_tgt=dsa-1420612970129&hsa_kw=&hsa_mt=&hsa_net=adwords&hsa_ver=3&gad_source=1&gclid=CjwKCAjwupGyBhBBEiwA0UcqaAsupePIgfV9o7DuvievNKdrcDdwmIovJ9xsYSoGgg6Qjq1L_u2PxRoCC6sQAvD_BwE";
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(payProUrl));
        startActivity(webIntent);
    }
}
