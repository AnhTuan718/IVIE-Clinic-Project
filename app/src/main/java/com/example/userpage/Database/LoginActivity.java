package com.example.userpage.Database;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.userpage.Admin.AdminDashboardActivity;
import com.example.userpage.Doctor.DoctorMainActivity;
import androidx.appcompat.app.AppCompatActivity;

import com.example.userpage.NavigationActivity;
import com.example.userpage.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_CLIENT_KEY = "clientKey";

    private EditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private TextView tvGoToRegister, tvForgotPassword;
    private ImageView ivGoogleLogin, ivFacebookLogin;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private DatabaseReference emailToClientKeyRef;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "LoginActivity onCreate started");
        setContentView(R.layout.activity_login);
        Log.d(TAG, "LoginActivity layout set");

        if (isUserLoggedIn()) {
            navigateToMainScreen();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        emailToClientKeyRef = FirebaseDatabase.getInstance().getReference("emailToClientKey");
        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initializeViews();
        setupListeners();
        Log.d(TAG, "LoginActivity onCreate completed");
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        ivGoogleLogin = findViewById(R.id.ivGoogleLogin);
        ivFacebookLogin = findViewById(R.id.ivFacebookLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(view -> validateAndLogin());

        tvGoToRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(view -> {
            // TODO: Xử lý quên mật khẩu (có thể thêm sau)
            Toast.makeText(this, "Chức năng quên mật khẩu đang được phát triển!", Toast.LENGTH_SHORT).show();
        });

        ivGoogleLogin.setOnClickListener(view -> {
            // TODO: Xử lý đăng nhập bằng Google (có thể thêm sau)
            Toast.makeText(this, "Đăng nhập bằng Google đang được phát triển!", Toast.LENGTH_SHORT).show();
        });

        ivFacebookLogin.setOnClickListener(view -> {
            // TODO: Xử lý đăng nhập bằng Facebook (có thể thêm sau)
            Toast.makeText(this, "Đăng nhập bằng Facebook đang được phát triển!", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean isUserLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void validateAndLogin() {
        String email = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Reset errors
        etUsername.setError(null);
        etPassword.setError(null);

        boolean hasError = false;
        if (email.isEmpty()) {
            etUsername.setError("Vui lòng nhập email");
            hasError = true;
        }

        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            hasError = true;
        }

        if (!hasError) {
            loginUser(email, password);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
            return networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        }
        return false;
    }

    private void loginUser(String email, String password) {
        Log.d(TAG, "Bắt đầu đăng nhập: " + email);

        if (!isNetworkAvailable()) {
            Log.e(TAG, "Không có kết nối mạng");
            Toast.makeText(this, "Không có kết nối mạng. Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Tìm clientKey từ emailToClientKey
                            String emailKey = email.replace(".", ",");
                            emailToClientKeyRef.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String clientKey = dataSnapshot.getValue(String.class);
                                        if (clientKey != null) {
                                            // Truy vấn dữ liệu người dùng từ users/clientX
                                            databaseReference.child(clientKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot userSnapshot) {
                                                    if (userSnapshot.exists()) {
                                                        // Lấy role từ dữ liệu người dùng
                                                        String role = userSnapshot.child("role").getValue(String.class);
                                                        if (role != null) {
                                                            Log.d(TAG, "Đăng nhập thành công với role: " + role);
                                                            saveLoginState(email, role, clientKey);
                                                            navigateToMainScreen(role);
                                                        } else {
                                                            progressBar.setVisibility(View.GONE);
                                                            Log.e(TAG, "Không tìm thấy role trong dữ liệu người dùng");
                                                            Toast.makeText(LoginActivity.this, "Không tìm thấy vai trò người dùng!", Toast.LENGTH_LONG).show();
                                                        }
                                                    } else {
                                                        progressBar.setVisibility(View.GONE);
                                                        Log.e(TAG, "Không tìm thấy dữ liệu người dùng trong cơ sở dữ liệu");
                                                        Toast.makeText(LoginActivity.this, "Không tìm thấy dữ liệu người dùng. Vui lòng đăng ký trước!", Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Log.e(TAG, "Đăng nhập thất bại: " + databaseError.getMessage());
                                                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            Log.e(TAG, "Không tìm thấy clientKey");
                                            Toast.makeText(LoginActivity.this, "Không tìm thấy dữ liệu người dùng. Vui lòng đăng ký trước!", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Log.e(TAG, "Không tìm thấy ánh xạ email");
                                        Toast.makeText(LoginActivity.this, "Không tìm thấy dữ liệu người dùng. Vui lòng đăng ký trước!", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    progressBar.setVisibility(View.GONE);
                                    Log.e(TAG, "Đăng nhập thất bại: " + databaseError.getMessage());
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "Lỗi: Không thể lấy userId");
                            Toast.makeText(this, "Lỗi: Không thể lấy userId", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Đăng nhập thất bại";
                        Log.e(TAG, "Đăng nhập thất bại: " + errorMessage);
                        Toast.makeText(this, "Đăng nhập thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveLoginState(String email, String role, String clientKey) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_ROLE, role);
        editor.putString(KEY_CLIENT_KEY, clientKey);
        editor.apply();
    }

    private void navigateToMainScreen() {
        String role = prefs.getString(KEY_USER_ROLE, null);
        navigateToMainScreen(role);
    }

    private void navigateToMainScreen(String role) {
        Intent intent;
        if (role == null) {
            Toast.makeText(this, "Không tìm thấy vai trò người dùng!", Toast.LENGTH_LONG).show();
            mAuth.signOut();
            prefs.edit().clear().apply();
            return;
        }

        switch (role) {
            case "user":
                intent = new Intent(LoginActivity.this, NavigationActivity.class);
                intent.putExtra("SELECTED_TAB", R.id.nav_home); // Chuyển đến tab TrangChuFragment
                break;
            case "doctor":
                intent = new Intent(LoginActivity.this, DoctorMainActivity.class);
                break;
            case "admin":
                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                break;
            default:
                Toast.makeText(this, "Vai trò không hợp lệ! Chỉ bệnh nhân, bác sĩ và quản trị viên được phép đăng nhập.", Toast.LENGTH_LONG).show();
                mAuth.signOut();
                prefs.edit().clear().apply();
                return;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
    private void checkUserRole(String email, String clientKey) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(clientKey);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String role = dataSnapshot.child("role").getValue(String.class);
                    Log.d("DEBUG_ROLE", "User role: " + role);
                    Log.d("DEBUG_ROLE", "Client key: " + clientKey);
                } else {
                    Log.d("DEBUG_ROLE", "User data not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DEBUG_ROLE", "Error: " + error.getMessage());
            }
        });
    }

}