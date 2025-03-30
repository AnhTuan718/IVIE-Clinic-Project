package com.example.userpage.Admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.userpage.Model.Medicine;
import com.example.userpage.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddMedicineActivity extends AppCompatActivity {

    private EditText etMedicineName, etMedicineDescription;
    private Button btnAddMedicine;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        // Thiết lập toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.add_medicine));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ các view
        etMedicineName = findViewById(R.id.et_medicine_name);
        etMedicineDescription = findViewById(R.id.et_medicine_description);
        btnAddMedicine = findViewById(R.id.btn_add_medicine);
        progressBar = findViewById(R.id.progress_bar);

        // Thiết lập sự kiện click cho nút thêm thuốc
        btnAddMedicine.setOnClickListener(v -> addMedicine());
    }

    private void addMedicine() {
        String name = etMedicineName.getText().toString().trim();
        String description = etMedicineDescription.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Medicine medicine = new Medicine(name, description);
        db.collection("medicines")
                .add(medicine)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Thêm thuốc thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}