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
import com.google.firebase.firestore.FirebaseFirestore;

public class EditDoctorActivity extends AppCompatActivity {

    private EditText fullNameEditText, specialtyEditText, experience1EditText, experience2EditText,
            hospitalEditText, onlineConsultationPlaceEditText, clinicAddressEditText;
    private Button updateDoctorButton;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private String doctorId;
    private Doctor currentDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctor);

        // Lấy ID bác sĩ từ Intent
        doctorId = getIntent().getStringExtra("DOCTOR_ID");
        if (doctorId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin bác sĩ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thiết lập toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chỉnh sửa thông tin bác sĩ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ các view
        fullNameEditText = findViewById(R.id.doctor_fullname);
        specialtyEditText = findViewById(R.id.doctor_specialty);
        experience1EditText = findViewById(R.id.doctor_experience1);
        experience2EditText = findViewById(R.id.doctor_experience2);
        hospitalEditText = findViewById(R.id.doctor_hospital);
        onlineConsultationPlaceEditText = findViewById(R.id.doctor_online_consultation);
        clinicAddressEditText = findViewById(R.id.doctor_clinic_address);
        updateDoctorButton = findViewById(R.id.update_doctor_button);
        progressBar = findViewById(R.id.progress_bar);

        // Tải thông tin bác sĩ
        loadDoctorInfo();

        // Thiết lập sự kiện click cho nút cập nhật
        updateDoctorButton.setOnClickListener(v -> updateDoctor());
    }

    private void loadDoctorInfo() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("doctors").document(doctorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    if (documentSnapshot.exists()) {
                        currentDoctor = documentSnapshot.toObject(Doctor.class);
                        currentDoctor.setUserId(documentSnapshot.getId());

                        // Hiển thị thông tin bác sĩ lên form
                        fullNameEditText.setText(currentDoctor.getName());
                        specialtyEditText.setText(currentDoctor.getSpecialization());
                        experience1EditText.setText(currentDoctor.getExperience1());
                        experience2EditText.setText(currentDoctor.getExperience2());
                        hospitalEditText.setText(currentDoctor.getWorkplace());
                        onlineConsultationPlaceEditText.setText(currentDoctor.getOnlineConsultationPlace());
                        clinicAddressEditText.setText(currentDoctor.getClinicAddress());
                    } else {
                        Toast.makeText(EditDoctorActivity.this,
                                "Không tìm thấy thông tin bác sĩ", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditDoctorActivity.this,
                            "Lỗi khi tải thông tin bác sĩ: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void updateDoctor() {
        String fullName = fullNameEditText.getText().toString().trim();
        String specialty = specialtyEditText.getText().toString().trim();
        String experience1 = experience1EditText.getText().toString().trim();
        String experience2 = experience2EditText.getText().toString().trim();
        String hospital = hospitalEditText.getText().toString().trim();
        String onlineConsultationPlace = onlineConsultationPlaceEditText.getText().toString().trim();
        String clinicAddress = clinicAddressEditText.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (fullName.isEmpty() || specialty.isEmpty() || hospital.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Cập nhật thông tin bác sĩ
        Doctor updatedDoctor = new Doctor(
                doctorId,
                fullName,
                specialty,
                experience1,
                experience2,
                hospital,
                onlineConsultationPlace,
                clinicAddress
        );

        db.collection("doctors").document(doctorId)
                .set(updatedDoctor)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditDoctorActivity.this,
                            "Cập nhật thông tin bác sĩ thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditDoctorActivity.this,
                            "Lỗi khi cập nhật thông tin bác sĩ: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}

