package com.example.userpage.Doctor;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.userpage.Model.Doctor;
import com.example.userpage.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorEditProfileActivity extends AppCompatActivity {

    private EditText etDoctorFullName, etDoctorSpecialty, etDoctorExperience1, etDoctorExperience2;
    private EditText etDoctorHospital, etOnlineConsultationPlace, etClinicAddress;
    private MaterialButton btnSaveProfile;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private String clientKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_edit_profile);

        // Ánh xạ các thành phần giao diện
        etDoctorFullName = findViewById(R.id.etDoctorFullName);
        etDoctorSpecialty = findViewById(R.id.etDoctorSpecialty);
        etDoctorExperience1 = findViewById(R.id.etDoctorExperience1);
        etDoctorExperience2 = findViewById(R.id.etDoctorExperience2);
        etDoctorHospital = findViewById(R.id.etDoctorHospital);
        etOnlineConsultationPlace = findViewById(R.id.etOnlineConsultationPlace);
        etClinicAddress = findViewById(R.id.etClinicAddress);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        progressBar = findViewById(R.id.progressBar);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Lấy clientKey từ Intent (thay vì SharedPreferences để đồng bộ với DoctorManagementActivity)
        clientKey = getIntent().getStringExtra("DOCTOR_ID");
        if (clientKey == null) {
            Toast.makeText(this, "Không tìm thấy thông tin bác sĩ!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Load thông tin hiện tại của bác sĩ
        loadDoctorProfile();

        // Xử lý sự kiện nút Lưu
        btnSaveProfile.setOnClickListener(view -> saveDoctorProfile());
    }

    private void loadDoctorProfile() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("doctors").document(clientKey).get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Doctor doctor = document.toObject(Doctor.class);
                            if (doctor != null) {
                                etDoctorFullName.setText(doctor.getName()); // Sửa từ getFullName() thành getName()
                                etDoctorSpecialty.setText(doctor.getSpecialization()); // Sửa từ getSpecialty() thành getSpecialization()
                                etDoctorExperience1.setText(doctor.getExperience1());
                                etDoctorExperience2.setText(doctor.getExperience2());
                                etDoctorHospital.setText(doctor.getWorkplace()); // Sửa từ getHospital() thành getWorkplace()
                                etOnlineConsultationPlace.setText(doctor.getOnlineConsultationPlace());
                                etClinicAddress.setText(doctor.getClinicAddress());
                            }
                        } else {
                            Toast.makeText(DoctorEditProfileActivity.this, "Không tìm thấy thông tin bác sĩ!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(DoctorEditProfileActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveDoctorProfile() {
        String name = etDoctorFullName.getText().toString().trim();
        String specialization = etDoctorSpecialty.getText().toString().trim();
        String experience1 = etDoctorExperience1.getText().toString().trim();
        String experience2 = etDoctorExperience2.getText().toString().trim();
        String workplace = etDoctorHospital.getText().toString().trim();
        String onlineConsultationPlace = etOnlineConsultationPlace.getText().toString().trim();
        String clinicAddress = etClinicAddress.getText().toString().trim();

        // Kiểm tra dữ liệu
        if (name.isEmpty() || specialization.isEmpty() || experience1.isEmpty() || experience2.isEmpty() ||
                workplace.isEmpty() || onlineConsultationPlace.isEmpty() || clinicAddress.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Cập nhật thông tin bác sĩ
        Doctor updatedDoctor = new Doctor(clientKey, name, specialization, experience1, experience2, workplace,
                onlineConsultationPlace, clinicAddress);
        db.collection("doctors").document(clientKey).set(updatedDoctor)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(DoctorEditProfileActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(DoctorEditProfileActivity.this, "Cập nhật thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}