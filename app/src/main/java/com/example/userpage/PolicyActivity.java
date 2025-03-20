package com.example.userpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class PolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        // Initialize back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        // Set up click listeners for navigation
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Navigate to AboutIVIEActivity when clicking "Về IVIE - Bác sĩ ơi"
        findViewById(R.id.layoutAboutIVIE).setOnClickListener(v -> {
            Intent intent = new Intent(PolicyActivity.this, AboutIVIEActivity.class);
            startActivity(intent);
        });

        // Navigate to TermsOfUseActivity when clicking "Điều khoản sử dụng"
        findViewById(R.id.layoutTermsOfUse).setOnClickListener(v -> {
            Intent intent = new Intent(PolicyActivity.this, TermsOfUseActivity.class);
            startActivity(intent);
        });
    }
}
