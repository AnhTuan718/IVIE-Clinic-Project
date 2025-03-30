package com.example.userpage.Admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.userpage.Model.Doctor;
import com.example.userpage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddDoctorActivity extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, passwordEditText, specialtyEditText,
            experience1EditText, experience2EditText, hospitalEditText,
            onlineConsultationPlaceEditText, clinicAddressEditText;
    private Button addDoctorButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        // Thiết lập toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thêm bác sĩ mới");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ các view
        fullNameEditText = findViewById(R.id.doctor_fullname);
        emailEditText = findViewById(R.id.doctor_email);
        passwordEditText = findViewById(R.id.doctor_password);
        specialtyEditText = findViewById(R.id.doctor_specialty);
        experience1EditText = findViewById(R.id.doctor_experience1);
        experience2EditText = findViewById(R.id.doctor_experience2);
        hospitalEditText = findViewById(R.id.doctor_hospital);
        onlineConsultationPlaceEditText = findViewById(R.id.doctor_online_consultation);
        clinicAddressEditText = findViewById(R.id.doctor_clinic_address);
        addDoctorButton = findViewById(R.id.add_doctor_button);
        progressBar = findViewById(R.id.progress_bar);

        // Thiết lập sự kiện click cho nút thêm bác sĩ
        addDoctorButton.setOnClickListener(v -> addDoctor());
    }

    private void addDoctor() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String specialty = specialtyEditText.getText().toString().trim();
        String experience1 = experience1EditText.getText().toString().trim();
        String experience2 = experience2EditText.getText().toString().trim();
        String hospital = hospitalEditText.getText().toString().trim();
        String onlineConsultationPlace = onlineConsultationPlaceEditText.getText().toString().trim();
        String clinicAddress = clinicAddressEditText.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || specialty.isEmpty() || hospital.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.length() < 6) {
            passwordEditText.setError("Mật khẩu phải có ít nhất 6 ký tự");
            passwordEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Tạo tài khoản cho bác sĩ
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = task.getResult().getUser().getUid();

                        // Thêm role cho bác sĩ
                        Map<String, Object> userRole = new HashMap<>();
                        userRole.put("role", "doctor");

                        db.collection("users").document(userId).set(userRole)
                                .addOnSuccessListener(aVoid -> {
                                    // Lưu thông tin bác sĩ
                                    Doctor doctor = new Doctor(
                                            userId,
                                            fullName,
                                            specialty,
                                            experience1,
                                            experience2,
                                            hospital,
                                            onlineConsultationPlace,
                                            clinicAddress
                                    );

                                    // Lưu thông tin bác sĩ vào Firestore
                                    db.collection("doctors").document(userId).set(doctor)
                                            .addOnSuccessListener(aVoid1 -> {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(AddDoctorActivity.this,
                                                        "Thêm bác sĩ thành công", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(AddDoctorActivity.this,
                                                        "Lỗi khi lưu thông tin bác sĩ: " + e.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(AddDoctorActivity.this,
                                            "Lỗi khi thêm role: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddDoctorActivity.this,
                                "Lỗi khi tạo tài khoản: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        }
    }


