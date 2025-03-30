package com.example.userpage.Doctor;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.userpage.Model.Doctor;
import com.example.userpage.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorDetailActivity extends AppCompatActivity {

    private ImageView ivDoctorAvatar;
    private TextView tvDoctorName, tvDoctorSpecialty, tvDoctorWorkplace;
    private TextView tvWorkplaceLabel, tvOnlineConsultationLabel;
    private TextView tvExperience1, tvExperience2;
    private TextView tvClinicAddressTitle, tvClinicAddress;
    private MaterialButton btnConsultOnline;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        // Ánh xạ các thành phần giao diện
        ivDoctorAvatar = findViewById(R.id.ivDoctorAvatar);
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvDoctorSpecialty = findViewById(R.id.tvDoctorSpecialty);
        tvDoctorWorkplace = findViewById(R.id.tvDoctorWorkplace);
        tvWorkplaceLabel = findViewById(R.id.tvWorkplaceLabel);
        tvOnlineConsultationLabel = findViewById(R.id.tvOnlineConsultationLabel);
        tvExperience1 = findViewById(R.id.tvExperience1);
        tvExperience2 = findViewById(R.id.tvExperience2);
        tvClinicAddressTitle = findViewById(R.id.tvClinicAddressTitle);
        tvClinicAddress = findViewById(R.id.tvClinicAddress);
        btnConsultOnline = findViewById(R.id.btnConsultOnline);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Lấy doctorId từ Intent
        String doctorId = getIntent().getStringExtra("DOCTOR_ID");
        if (doctorId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin bác sĩ!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Load thông tin bác sĩ
        loadDoctorInfo(doctorId);

        // Xử lý sự kiện nút "Tư vấn trực tuyến" (placeholder)
        btnConsultOnline.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng tư vấn trực tuyến đang được phát triển!", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadDoctorInfo(String doctorId) {
        db.collection("doctors").document(doctorId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Doctor doctor = document.toObject(Doctor.class);
                            if (doctor != null) {
                                // Cập nhật thông tin bác sĩ lên giao diện
                                tvDoctorName.setText(doctor.getName()); // Sửa từ getFullName() thành getName()
                                tvDoctorSpecialty.setText(doctor.getSpecialization()); // Sửa từ getSpecialty() thành getSpecialization()
                                tvDoctorWorkplace.setText(doctor.getWorkplace()); // Sửa từ getHospital() thành getWorkplace()
                                tvWorkplaceLabel.setText(doctor.getWorkplace()); // Sửa từ getHospital() thành getWorkplace()
                                tvOnlineConsultationLabel.setText(doctor.getOnlineConsultationPlace());
                                tvExperience1.setText(doctor.getExperience1());
                                tvExperience2.setText(doctor.getExperience2());
                                tvClinicAddressTitle.setText(doctor.getOnlineConsultationPlace());
                                tvClinicAddress.setText(doctor.getClinicAddress());
                            }
                        } else {
                            Toast.makeText(DoctorDetailActivity.this, "Không tìm thấy thông tin bác sĩ!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(DoctorDetailActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
}