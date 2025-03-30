package com.example.userpage.Admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.userpage.Model.Medicine;
import com.example.userpage.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class MedicineDetailActivity extends AppCompatActivity {

    private TextView tvMedicineName, tvMedicineDescription;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);

        // Thiết lập toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.medicine_detail));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ các view
        tvMedicineName = findViewById(R.id.tv_medicine_name);
        tvMedicineDescription = findViewById(R.id.tv_medicine_description);

        // Lấy medicineId từ intent
        String medicineId = getIntent().getStringExtra("MEDICINE_ID");
        if (medicineId != null) {
            loadMedicineDetails(medicineId);
        }
    }

    private void loadMedicineDetails(String medicineId) {
        db.collection("medicines").document(medicineId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Medicine medicine = documentSnapshot.toObject(Medicine.class);
                        if (medicine != null) {
                            tvMedicineName.setText(medicine.getName());
                            tvMedicineDescription.setText(medicine.getDescription());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}