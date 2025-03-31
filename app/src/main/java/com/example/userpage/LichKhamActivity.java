package com.example.userpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LichKhamActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton btnBack;
    private TextView tvDoctorInfo;

    private final String[] tabs = new String[]{"Chờ duyệt", "Đã duyệt", "Đang khám", "Hoàn thành", "Quá hẹn", "Đã hủy"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_kham);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnBack = findViewById(R.id.btnBack);
        tvDoctorInfo = findViewById(R.id.tv_doctor_info);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String doctorName = intent.getStringExtra("doctor_name");
        String doctorSpecialty = intent.getStringExtra("doctor_specialty");
        if (doctorName != null && doctorSpecialty != null) {
            tvDoctorInfo.setText("Đặt lịch với: " + doctorName + " - " + doctorSpecialty);
        } else {
            tvDoctorInfo.setText("Không có thông tin bác sĩ");
        }

        setUpViewPager();

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
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_order_tab, container, false);
        }
    }
}