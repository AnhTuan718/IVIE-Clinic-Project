package com.example.userpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.userpage.Adapter.AppointmentAdapter;
import com.example.userpage.Model.Appointment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LichKhamActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton btnBack;
    private TextView tvDoctorInfo, tvAppointmentStatus;

    private final String[] tabs = new String[]{"Chờ duyệt", "Đã duyệt", "Đang khám", "Hoàn thành", "Quá hạn", "Đã hủy"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_kham);

        // Ánh xạ các view
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnBack = findViewById(R.id.btnBack);
        tvDoctorInfo = findViewById(R.id.tv_doctor_info);
        tvAppointmentStatus = findViewById(R.id.tv_appointment_status);

        // Thiết lập ViewPager và adapter
        setUpViewPager();

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String appointmentStatus = intent.getStringExtra("appointment_status");

        if (appointmentStatus != null) {
            // Hiển thị trạng thái
            tvAppointmentStatus.setVisibility(View.VISIBLE);
            tvAppointmentStatus.setText("Trạng thái: " + appointmentStatus);

            // Chọn tab dựa trên trạng thái
            for (int i = 0; i < tabs.length; i++) {
                if (tabs[i].equals(appointmentStatus)) {
                    viewPager.setCurrentItem(i);
                    break;
                }
            }
        } else {
            // Mặc định hiển thị tab đầu tiên
            viewPager.setCurrentItem(0);
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void setUpViewPager() {
        OrderTabAdapter adapter = new OrderTabAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabs[position])).attach();
    }

    // Adapter class
    private static class OrderTabAdapter extends FragmentStateAdapter {
        public OrderTabAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            OrderTabFragment fragment = new OrderTabFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 6; // Number of tabs
        }
    }

    // Fragment hiển thị danh sách tab
    public static class OrderTabFragment extends Fragment {
        private RecyclerView recyclerView;
        private AppointmentAdapter adapter;
        private List<Appointment> appointmentList;
        private TextView tvEmpty;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_order_tab, container, false);
            recyclerView = view.findViewById(R.id.recyclerView);
            tvEmpty = view.findViewById(R.id.tv_empty);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Lấy vị trí tab
            Bundle args = getArguments();
            int position = args != null ? args.getInt("position", 0) : 0;
            String status = "";

            // Ánh xạ vị trí tab với trạng thái
            switch (position) {
                case 0: status = "Chờ duyệt"; break;
                case 1: status = "Đã duyệt"; break;
                case 2: status = "Đang khám"; break;
                case 3: status = "Hoàn thành"; break;
                case 4: status = "Quá hạn"; break;
                case 5: status = "Đã hủy"; break;
            }

            appointmentList = new ArrayList<>();
            adapter = new AppointmentAdapter(appointmentList);
            recyclerView.setAdapter(adapter);

            // Đây là điểm quan trọng - tải dữ liệu theo trạng thái
            loadAppointments(status);

            return view;
        }

        private void loadAppointments(String status) {
            if (getContext() == null) return;

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("appointments");
            ref.orderByChild("status").equalTo(status).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    appointmentList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Appointment appointment = ds.getValue(Appointment.class);
                        if (appointment != null) {
                            // Thêm ID để có thể thao tác với item sau này
                            appointment.setId(ds.getKey());
                            appointmentList.add(appointment);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    // Hiển thị thông báo nếu không có dữ liệu
                    if (appointmentList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        tvEmpty.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}