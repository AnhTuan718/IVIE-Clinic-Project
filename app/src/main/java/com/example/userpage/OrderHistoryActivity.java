package com.example.userpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderHistoryActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton btnBack;

    // Tab titles
    private String[] tabs = new String[]{"Tất cả", "Đang xử lý", "Đã xác nhận", "Đang giao hàng",
            "Hoàn thành", "Đã hủy", "Yêu cầu mua thuốc"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Initialize views
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnBack = findViewById(R.id.btnBack);

        // Set up ViewPager with tabs
        OrderTabAdapter adapter = new OrderTabAdapter(this);
        viewPager.setAdapter(adapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabs[position])
        ).attach();

        // Back button click listener
        btnBack.setOnClickListener(v -> finish());
    }

    // Adapter for the tabs
    private class OrderTabAdapter extends FragmentStateAdapter {
        public OrderTabAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return the appropriate fragment for each tab position
            return new OrderTabFragment();
        }

        @Override
        public int getItemCount() {
            return tabs.length;
        }
    }

    // Fragment for each tab
    public static class OrderTabFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Using the empty state layout for demonstration
            return inflater.inflate(R.layout.layout_empty_state, container, false);
        }
    }
}
