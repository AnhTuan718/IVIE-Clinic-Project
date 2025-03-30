package com.example.userpage.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.userpage.Database.LoginActivity;
import com.example.userpage.Doctor.DoctorManagementActivity;
import com.example.userpage.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private CardView manageDoctorsCard, manageMedicinesCard, viewStatisticsCard, settingsCard;
    private TextView adminNameTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ các view
        adminNameTextView = findViewById(R.id.admin_name);
        manageDoctorsCard = findViewById(R.id.manage_doctors_card);
        manageMedicinesCard = findViewById(R.id.manage_medicines_card);
        viewStatisticsCard = findViewById(R.id.view_statistics_card);
        settingsCard = findViewById(R.id.settings_card);

        // Hiển thị tên admin
        if (mAuth.getCurrentUser() != null) {
            adminNameTextView.setText("Xin chào, " + mAuth.getCurrentUser().getEmail());
        }

        // Thiết lập sự kiện click cho các card
        manageDoctorsCard.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, DoctorManagementActivity.class));
        });

        manageMedicinesCard.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, MedicineManagementActivity.class));
        });

        viewStatisticsCard.setOnClickListener(v -> {
            // Chức năng xem thống kê
            // TODO: Triển khai sau
        });

        settingsCard.setOnClickListener(v -> {
            // Chức năng cài đặt
            // TODO: Triển khai sau
        });
    }

    public void logoutAdmin(View view) {
        mAuth.signOut();
        startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
        finish();
    }
}

