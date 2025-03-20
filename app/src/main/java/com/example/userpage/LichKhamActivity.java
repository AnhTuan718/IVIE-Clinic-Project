package com.example.userpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

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
    private Button btnAppointment, btnFollowup;

    private boolean isFollowUpMode = false; // Biến trạng thái

    // Danh sách tab khi ở chế độ Lịch khám
    private final String[] tabs = new String[]{"Chờ duyệt", "Đã duyệt", "Đang khám", "Hoàn thành", "Quá hẹn", "Đã hủy"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_kham);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnBack = findViewById(R.id.btnBack);
        btnAppointment = findViewById(R.id.btnAppointment);
        btnFollowup = findViewById(R.id.btnFollowup);

        setUpViewPager(false);

        btnBack.setOnClickListener(v -> finish());

        btnAppointment.setOnClickListener(v -> {
            if (isFollowUpMode) {
                isFollowUpMode = false;
                setUpViewPager(false);
            }
        });

        // Xử lý nút "Lịch tái khám"
        btnFollowup.setOnClickListener(v -> {
            if (!isFollowUpMode) {
                isFollowUpMode = true;
                setUpViewPager(true);
            }
        });
    }

    private void setUpViewPager(boolean isFollowUp) {
        OrderTabAdapter adapter = new OrderTabAdapter(this, isFollowUp);
        viewPager.setAdapter(adapter);

        if (isFollowUp) {
            tabLayout.setVisibility(View.GONE); // Ẩn tab khi là Lịch tái khám
        } else {
            tabLayout.setVisibility(View.VISIBLE);
            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> tab.setText(tabs[position])
            ).attach();
        }
    }

    private static class OrderTabAdapter extends FragmentStateAdapter {
        private final boolean isFollowUp;

        public OrderTabAdapter(FragmentActivity fragmentActivity, boolean isFollowUp) {
            super(fragmentActivity);
            this.isFollowUp = isFollowUp;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return isFollowUp ? new EmptyFragment() : new OrderTabFragment();
        }

        @Override
        public int getItemCount() {
            return isFollowUp ? 1 : 6;
        }
    }

    // Fragment hiển thị danh sách tab
    public static class OrderTabFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.layout_empty_state2, container, false);
        }
    }

    // Fragment hiển thị "Không có dữ liệu"
    public static class EmptyFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.layout_no_data, container, false);
        }
    }
}
