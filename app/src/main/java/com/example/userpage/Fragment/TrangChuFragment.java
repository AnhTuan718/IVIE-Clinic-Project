package com.example.userpage.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userpage.Adapter.DoctorAdapter; // Corrected import
import com.example.userpage.Model.Doctor;
import com.example.userpage.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrangChuFragment extends Fragment {

    private RecyclerView rvFeaturedDoctors;
    private TextView tvViewMore;
    private DoctorAdapter adapter; // Updated to use the correct DoctorAdapter
    private List<Doctor> doctorList;
    private DatabaseReference doctorsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trang_chu, container, false);

        // Ánh xạ các thành phần giao diện
        rvFeaturedDoctors = view.findViewById(R.id.doctorRecyclerView);
        tvViewMore = view.findViewById(R.id.tvViewMore);

        // Khởi tạo RecyclerView
        doctorList = new ArrayList<>();
        adapter = new DoctorAdapter(getContext(), doctorList); // Pass context and list
        rvFeaturedDoctors.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFeaturedDoctors.setAdapter(adapter);

        // Khởi tạo Firebase Database
        doctorsRef = FirebaseDatabase.getInstance().getReference("doctors");

        // Load danh sách bác sĩ
        loadFeaturedDoctors();

        // Xử lý sự kiện nút "Xem thêm"
        tvViewMore.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chức năng xem thêm đang được phát triển!", Toast.LENGTH_SHORT).show();
        });

        // Placeholder cho ViewPager2 (banner)
        // ViewPager2 viewPager = view.findViewById(R.id.viewpager);
        // TODO: Tạo adapter cho ViewPager2 để hiển thị banner

        // Placeholder cho Chuyên khoa nổi bật
        RecyclerView rvSpecialties = view.findViewById(R.id.chuyenKhoaRecyclerView);
        rvSpecialties.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        // TODO: Triển khai adapter và load dữ liệu cho chuyên khoa nổi bật

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load lại danh sách bác sĩ khi fragment được hiển thị lại
        loadFeaturedDoctors();
    }

    public void updateDoctorsList(List<Doctor> doctors) {
        if (getActivity() == null || !isAdded()) {
            return;
        }

        try {
            if (doctorList != null && adapter != null) {
                doctorList.clear();
                doctorList.addAll(doctors);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e("TrangChuFragment", "Error updating doctors list: " + e.getMessage());
        }
    }

    private void loadFeaturedDoctors() {
        try {
            if (doctorsRef == null) {
                return;
            }

            doctorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (getActivity() == null || !isAdded()) {
                            return;
                        }

                        doctorList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Doctor doctor = snapshot.getValue(Doctor.class);
                            if (doctor != null) {
                                doctor.setUserId(snapshot.getKey());
                                doctorList.add(doctor);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("TrangChuFragment", "Error processing doctor data: " + e.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if (getActivity() != null && isAdded()) {
                        Toast.makeText(getContext(),
                                "Lỗi: " + databaseError.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("TrangChuFragment", "Error loading doctors: " + e.getMessage());
        }
    }
}