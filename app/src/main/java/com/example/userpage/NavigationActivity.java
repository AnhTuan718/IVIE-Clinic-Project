package com.example.userpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.userpage.Database.LoginActivity;
import com.example.userpage.Shopping.ShoppingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.userpage.Fragment.BeforeLoginFragment;
import com.example.userpage.Fragment.TrangChuFragment;
import com.example.userpage.Fragment.UserProfileFragment;

public class NavigationActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new TrangChuFragment();
            } else if (itemId == R.id.nav_community) {
                selectedFragment = new ShoppingFragment(); // Use ShoppingFragment
            } else if (itemId == R.id.nav_notifications) {
                // Chưa xử lý
                return true;
            } else if (itemId == R.id.nav_profile) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
                if (isLoggedIn) {
                    selectedFragment = new UserProfileFragment();
                } else {
                    selectedFragment = new BeforeLoginFragment();
                }
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new TrangChuFragment())
                    .commit();
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("SELECTED_TAB")) {
            int selectedTab = intent.getIntExtra("SELECTED_TAB", R.id.nav_home);
            bottomNavigation.setSelectedItemId(selectedTab);
        }
    }

    public void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        if (bottomNavigation.getSelectedItemId() == R.id.nav_profile) {
            Fragment selectedFragment = isLoggedIn ? new UserProfileFragment() : new BeforeLoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null && intent.hasExtra("SELECTED_TAB")) {
            int selectedTab = intent.getIntExtra("SELECTED_TAB", R.id.nav_home);
            bottomNavigation.setSelectedItemId(selectedTab);
        }
    }
}