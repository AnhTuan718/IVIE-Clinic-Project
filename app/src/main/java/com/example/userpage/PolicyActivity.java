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
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        setupClickListeners();
    }

    private void setupClickListeners() {
        findViewById(R.id.layoutAboutIVIE).setOnClickListener(v -> {
            Intent intent = new Intent(PolicyActivity.this, AboutIVIEActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.layoutTermsOfUse).setOnClickListener(v -> {
            Intent intent = new Intent(PolicyActivity.this, TermsOfUseActivity.class);
            startActivity(intent);
        });
    }
}
