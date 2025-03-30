package com.example.userpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.userpage.Database.LoginActivity;
import com.example.userpage.Model.Doctor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.userpage.Fragment.BeforeLoginFragment;
import com.example.userpage.Fragment.TrangChuFragment;
import com.example.userpage.Fragment.UserProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ROLE = "userRole";
    private BottomNavigationView bottomNavigation;
    private DatabaseReference doctorsRef;
    private ValueEventListener doctorsListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        mAuth = FirebaseAuth.getInstance();
        bottomNavigation = findViewById(R.id.bottomNavigation);
        setupBottomNavigation();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            initializeFirebase();
        }

        if (savedInstanceState == null) {
            if (currentUser == null) {
                // Nếu chưa đăng nhập, hiển thị BeforeLoginFragment
                loadFragment(new BeforeLoginFragment());
                bottomNavigation.setSelectedItemId(R.id.nav_profile);
            } else {
                loadInitialFragment();
            }
        }

        handleIntent(getIntent());
    }

    private void initializeFirebase() {
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null && currentUser.isEmailVerified()) {
                currentUser.getIdToken(true)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String email = currentUser.getEmail();
                                if (email != null) {
                                    String emailKey = email.replace(".", ",");
                                    DatabaseReference emailToClientKeyRef = FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("emailToClientKey")
                                            .child(emailKey);

                                    emailToClientKeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String clientKey = snapshot.getValue(String.class);
                                            if (clientKey != null) {
                                                DatabaseReference userRoleRef = FirebaseDatabase.getInstance()
                                                        .getReference()
                                                        .child("users")
                                                        .child(clientKey)
                                                        .child("role");

                                                userRoleRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        String role = snapshot.getValue(String.class);
                                                        if ("admin".equals(role) || "doctor".equals(role)) {
                                                            doctorsRef = FirebaseDatabase.getInstance().getReference().child("doctors");
                                                            loadDoctorsData();
                                                            // Di chuyển việc ghi SharedPreferences ra khỏi main thread nếu cần
                                                            new Thread(() -> {
                                                                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                                                                editor.putString(KEY_USER_ROLE, role);
                                                                editor.apply();
                                                            }).start();
                                                        } else {
                                                            Log.w("NavigationActivity", "User role not authorized: " + role);
                                                            showError("Bạn không có quyền truy cập vào dữ liệu này");
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Log.e("NavigationActivity", "Error getting user role: " + error.getMessage());
                                                        showError("Không thể xác thực quyền truy cập");
                                                    }
                                                });
                                            } else {
                                                Log.e("NavigationActivity", "Client key not found for email: " + email);
                                                showError("Không tìm thấy thông tin người dùng");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("NavigationActivity", "Error getting client key: " + error.getMessage());
                                            showError("Không thể xác thực người dùng");
                                        }
                                    });
                                }
                            } else {
                                Log.e("NavigationActivity", "Error getting ID token", task.getException());
                                showError("Lỗi xác thực, vui lòng đăng nhập lại");
                            }
                        });
            } else {
                Log.d("NavigationActivity", "User not authenticated or email not verified");
                if (doctorsListener != null && doctorsRef != null) {
                    doctorsRef.removeEventListener(doctorsListener);
                    doctorsListener = null;
                }
            }
        } catch (Exception e) {
            Log.e("NavigationActivity", "Error in initializeFirebase: " + e.getMessage());
            showError("Có lỗi xảy ra khi khởi tạo dữ liệu");
        }
    }

    private void loadDoctorsData() {
        if (doctorsRef == null) {
            Log.e("NavigationActivity", "doctorsRef is null");
            return;
        }
        // Kiểm tra nếu người dùng có quyền truy cập
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("NavigationActivity", "User not authenticated");
            return;
        }
        try {
            if (doctorsListener != null) {
                doctorsRef.removeEventListener(doctorsListener);
            }

            doctorsListener = new ValueEventListener() {
                private static final int MAX_RETRIES = 3;
                private int retryCount = 0;

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    new Thread(() -> {
                        try {
                            List<Doctor> doctors = new ArrayList<>();
                            for (DataSnapshot doctorSnapshot : snapshot.getChildren()) {
                                try {
                                    Doctor doctor = doctorSnapshot.getValue(Doctor.class);
                                    if (doctor != null) {
                                        doctors.add(doctor);
                                    }
                                } catch (Exception e) {
                                    Log.e("NavigationActivity", "Error parsing doctor data: " + e.getMessage());
                                }
                            }

                            new Handler(Looper.getMainLooper()).post(() -> {
                                try {
                                    updateDoctorsList(doctors);
                                } catch (Exception e) {
                                    Log.e("NavigationActivity", "Error updating UI: " + e.getMessage());
                                    showError("Không thể cập nhật danh sách bác sĩ");
                                }
                            });
                        } catch (Exception e) {
                            Log.e("NavigationActivity", "Error processing doctors data: " + e.getMessage());
                            showError("Có lỗi xảy ra khi xử lý dữ liệu");
                        }
                    }).start();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("NavigationActivity", "Failed to load doctors: " + error.getMessage());
                    if (retryCount < MAX_RETRIES && error.getCode() == DatabaseError.PERMISSION_DENIED) {
                        retryCount++;
                        Log.d("NavigationActivity", "Retrying... Attempt " + retryCount);
                        // Retry after a delay
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (!isFinishing()) {
                                loadDoctorsData();
                            }
                        }, 1000 * retryCount); // Increasing delay for each retry
                    } else {
                        showError("Không thể tải danh sách bác sĩ. Vui lòng kiểm tra quyền truy cập.");
                    }
                }
            };

            doctorsRef.addValueEventListener(doctorsListener);
        } catch (Exception e) {
            Log.e("NavigationActivity", "Error setting up doctors listener: " + e.getMessage());
            showError("Có lỗi xảy ra khi thiết lập kết nối");
        }
    }

    private void updateDoctorsList(List<Doctor> doctors) {
        // Cập nhật UI với danh sách bác sĩ
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof TrangChuFragment) {
            ((TrangChuFragment) currentFragment).updateDoctorsList(doctors);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new TrangChuFragment();
            } else if (itemId == R.id.nav_community) {
                // Chưa xử lý
                return true;
            } else if (itemId == R.id.nav_notifications) {
                // Chưa xử lý
                return true;
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = getProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    private Fragment getProfileFragment() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        return isLoggedIn ? new UserProfileFragment() : new BeforeLoginFragment();
    }

    private void loadFragment(Fragment fragment) {
        try {
            if (!isFinishing() && !isDestroyed()) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commitAllowingStateLoss(); // Sử dụng commitAllowingStateLoss thay vì commit
            }
        } catch (Exception e) {
            Log.e("NavigationActivity", "Error loading fragment: " + e.getMessage());
        }
    }

    private void loadInitialFragment() {
        loadFragment(new TrangChuFragment());
        bottomNavigation.setSelectedItemId(R.id.nav_home);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("SELECTED_TAB")) {
            int selectedTab = intent.getIntExtra("SELECTED_TAB", R.id.nav_home);
            bottomNavigation.setSelectedItemId(selectedTab);
        }
    }

    public void navigateToLogin() {
        try {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean("IS_NAVIGATING_TO_LOGIN", true);
            editor.apply();

            if (!isFinishing() && !isDestroyed()) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("NavigationActivity", "Error starting LoginActivity: " + e.getMessage());
            showError("Không thể mở trang đăng nhập");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Kiểm tra xác thực hiện tại
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Xóa tham chiếu đến doctorsRef nếu không còn xác thực
            if (doctorsRef != null && doctorsListener != null) {
                doctorsRef.removeEventListener(doctorsListener);
                doctorsListener = null;
                doctorsRef = null;
            }
        } else {
            // Người dùng đã đăng nhập, kiểm tra nếu cần khởi tạo lại Firebase
            if (doctorsRef == null) {
                initializeFirebase();
            }
        }

        // Phần còn lại của mã...
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (doctorsRef != null && doctorsListener != null) {
            doctorsRef.removeEventListener(doctorsListener);
        }
    }

    private void showError(String message) {
        if (!isFinishing()) {
            new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            );
        }
    }
}