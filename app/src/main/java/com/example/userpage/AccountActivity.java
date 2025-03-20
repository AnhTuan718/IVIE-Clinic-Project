package com.example.userpage;
import android.content.Intent;
import android.os.Bundle;

import android.widget.ImageButton;

import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        setupClickListeners();
    }

    private void setupClickListeners() {
        // Change Password
        findViewById(R.id.layoutChangePassword).setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Logout
        findViewById(R.id.layoutLogout).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Đóng màn hình AccountActivity
                    })
                    .setNegativeButton("Hủy", null)
                    .show();

        });
    }
}
