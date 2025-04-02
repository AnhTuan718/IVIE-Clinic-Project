package com.example.userpage.Database;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.userpage.NavigationActivity;
import com.example.userpage.R;
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

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private DatabaseReference emailToClientKeyRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (isUserLoggedIn()) {
            navigateToMainScreen();
            return;
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        emailToClientKeyRef = FirebaseDatabase.getInstance().getReference("emailToClientKey");
        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        progressBar = findViewById(R.id.progressBar);

        etUsername.setHint("Email");
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndLogin();
            }
        });

        tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void validateAndLogin() {
        String email = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        etUsername.setError(null);
        etPassword.setError(null);

        boolean hasError = false;
        if (TextUtils.isEmpty(email)) {
            etUsername.setError("Vui lòng nhập email");
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
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
                            String emailKey = email.replace(".", ",");
                            emailToClientKeyRef.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String clientKey = dataSnapshot.getValue(String.class);
                                        if (clientKey != null) {
                                            databaseReference.child(clientKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot userSnapshot) {
                                                    if (userSnapshot.exists()) {
                                                        Log.d(TAG, "Đăng nhập thành công");
                                                        saveLoginState(email);
                                                        navigateToMainScreen();
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

    private void saveLoginState(String email) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    private void navigateToMainScreen() {
        Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
        intent.putExtra("SELECTED_TAB", R.id.nav_home);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}