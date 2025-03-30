package com.example.userpage.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userpage.Adapter.MedicineAdapter;
import com.example.userpage.Model.Medicine;
import com.example.userpage.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MedicineManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicineAdapter adapter;
    private List<Medicine> medicineList;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_management);

        // Thiết lập toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quản lý thuốc");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ các view
        recyclerView = findViewById(R.id.medicines_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        FloatingActionButton addMedicineFab = findViewById(R.id.fab);

        // Thiết lập RecyclerView
        medicineList = new ArrayList<>();
        adapter = new MedicineAdapter(this, medicineList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Thiết lập sự kiện click cho nút thêm thuốc
        addMedicineFab.setOnClickListener(v -> {
            startActivity(new Intent(MedicineManagementActivity.this, AddMedicineActivity.class));
        });

        // Tải danh sách thuốc
        loadMedicines();

        // Thiết lập sự kiện click cho adapter
        adapter.setOnItemClickListener(position -> {
            Medicine medicine = medicineList.get(position);
            Intent intent = new Intent(MedicineManagementActivity.this, MedicineDetailActivity.class);
            intent.putExtra("MEDICINE_ID", medicine.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại danh sách thuốc khi quay lại màn hình
        loadMedicines();
    }

    private void loadMedicines() {
        progressBar.setVisibility(View.VISIBLE);
        medicineList.clear();

        db.collection("medicines")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Medicine medicine = document.toObject(Medicine.class);
                            medicine.setId(document.getId());
                            medicineList.add(medicine);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MedicineManagementActivity.this,
                                "Lỗi khi tải danh sách thuốc", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

