package com.example.userpage.Doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userpage.Adapter.AdminDoctorAdapter;
import com.example.userpage.Admin.AddDoctorActivity;
import com.example.userpage.Admin.EditDoctorActivity;
import com.example.userpage.Model.Doctor;
import com.example.userpage.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminDoctorAdapter adapter;
    private List<Doctor> doctorList;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_management);

        // Thiết lập toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.doctor_management_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ các view
        recyclerView = findViewById(R.id.doctors_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        FloatingActionButton addDoctorFab = findViewById(R.id.fab);

        // Thiết lập RecyclerView
        doctorList = new ArrayList<>();
        adapter = new AdminDoctorAdapter(this, doctorList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Thiết lập sự kiện click cho nút thêm bác sĩ
        addDoctorFab.setOnClickListener(v -> {
            startActivity(new Intent(DoctorManagementActivity.this, AddDoctorActivity.class));
        });

        // Tải danh sách bác sĩ
        loadDoctors();

        // Thiết lập sự kiện click cho adapter
        adapter.setOnItemClickListener(new AdminDoctorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Doctor doctor = doctorList.get(position);
                Intent intent = new Intent(DoctorManagementActivity.this, DoctorDetailActivity.class);
                intent.putExtra("DOCTOR_ID", doctor.getUserId());
                startActivity(intent);
            }

            @Override
            public void onEditClick(int position) {
                Doctor doctor = doctorList.get(position);
                Intent intent = new Intent(DoctorManagementActivity.this, EditDoctorActivity.class);
                intent.putExtra("DOCTOR_ID", doctor.getUserId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                Doctor doctor = doctorList.get(position);
                showDeleteConfirmationDialog(doctor);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDoctors();
    }

    private void loadDoctors() {
        progressBar.setVisibility(View.VISIBLE);
        doctorList.clear();

        db.collection("doctors")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Doctor doctor = document.toObject(Doctor.class);
                            doctor.setUserId(document.getId());
                            doctorList.add(doctor);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(DoctorManagementActivity.this,
                                getString(R.string.error_load_doctors), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDeleteConfirmationDialog(Doctor doctor) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.confirm_delete_title));
        builder.setMessage(getString(R.string.confirm_delete_message, doctor.getName()));
        builder.setPositiveButton(getString(R.string.delete), (dialog, which) -> {
            deleteDoctor(doctor.getUserId());
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteDoctor(String doctorId) {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("doctors").document(doctorId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DoctorManagementActivity.this,
                            getString(R.string.delete_doctor_success), Toast.LENGTH_SHORT).show();
                    loadDoctors();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DoctorManagementActivity.this,
                            getString(R.string.delete_doctor_error, e.getMessage()), Toast.LENGTH_SHORT).show();
                });
    }
}