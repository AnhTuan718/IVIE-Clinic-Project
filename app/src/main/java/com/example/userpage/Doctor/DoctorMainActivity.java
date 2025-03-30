package com.example.userpage.Doctor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.userpage.Database.LoginActivity;
import com.example.userpage.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class DoctorMainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private FirebaseAuth mAuth;

    private MaterialButton btnViewAppointments, btnViewPatients, btnEditProfile, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Kiểm tra vai trò người dùng
        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String role = prefs.getString("userRole", null);

        if (role == null || !role.equals("doctor")) {
            Toast.makeText(this, "Bạn không có quyền truy cập màn hình này!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Ánh xạ các thành phần giao diện
        initializeViews();
        setupListeners();

        // Hiển thị thông báo chào mừng
        Toast.makeText(this, "Chào mừng bác sĩ!", Toast.LENGTH_SHORT).show();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }

    private void initializeViews() {
        btnViewAppointments = findViewById(R.id.btnViewAppointments);
        btnViewPatients = findViewById(R.id.btnViewPatients);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupListeners() {
        btnViewAppointments.setOnClickListener(view -> {
            Intent intent = new Intent(DoctorMainActivity.this, DoctorLichKhamActivity.class);
            startActivity(intent);
        });

        btnViewPatients.setOnClickListener(view -> {
            // TODO: Tạo activity để xem danh sách bệnh nhân
            Toast.makeText(this, "Chức năng xem danh sách bệnh nhân đang được phát triển!", Toast.LENGTH_SHORT).show();
        });

        btnEditProfile.setOnClickListener(view -> {
            Intent intent = new Intent(DoctorMainActivity.this, DoctorEditProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(view -> {
            // Đăng xuất
            mAuth.signOut();
            prefs.edit().clear().apply();
            Intent intent = new Intent(DoctorMainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
        });
    }
}