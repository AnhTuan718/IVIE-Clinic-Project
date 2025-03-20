package com.example.userpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Tham chiếu view
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Sự kiện cho nút "Đăng nhập"
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                etUsername.setError(null);
                etPassword.setError(null);

                // Kiểm tra trường trống
                boolean hasError = false;
                if (TextUtils.isEmpty(username)) {
                    etUsername.setError("Vui lòng nhập tên đăng nhập");
                    hasError = true;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Vui lòng nhập mật khẩu");
                    hasError = true;
                }

                if (!hasError) {
                    // Lưu trạng thái đăng nhập
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(KEY_IS_LOGGED_IN, true);
                    editor.apply();

                    // Chuyển sang NavigationActivity và yêu cầu hiển thị mục "User"
                    Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                    intent.putExtra("SELECTED_TAB", R.id.nav_profile); // Gửi thông tin để chọn tab "User"
                    startActivity(intent);
                    finish(); // Đóng LoginActivity
                }
            }
        });

        // Chuyển sang màn hình đăng ký
        tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}