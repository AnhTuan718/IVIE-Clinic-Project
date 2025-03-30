package com.example.userpage.Doctor;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userpage.Model.Appointment;
import com.example.userpage.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorLichKhamActivity extends AppCompatActivity {

    private RecyclerView rvDoctorAppointments;
    private DoctorAppointmentAdapter adapter; // Correct type
    private List<Appointment> appointmentList;
    private DatabaseReference appointmentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_lich_kham);

        // Ánh xạ RecyclerView
        rvDoctorAppointments = findViewById(R.id.rvDoctorAppointments);

        // Khởi tạo danh sách lịch khám
        appointmentList = new ArrayList<>();

        // Khởi tạo adapter
        adapter = new DoctorAppointmentAdapter(appointmentList);

        // Thiết lập RecyclerView
        rvDoctorAppointments.setLayoutManager(new LinearLayoutManager(this));
        rvDoctorAppointments.setAdapter(adapter);

        // Khởi tạo Firebase Database
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        // Load danh sách lịch khám
        loadAppointments();
    }

    private void loadAppointments() {
        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        appointment.setId(snapshot.getKey());
                        appointmentList.add(appointment);
                    }
                }
                adapter.notifyDataSetChanged(); // Cập nhật RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorLichKhamActivity.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}