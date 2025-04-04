package com.example.userpage.Database;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.userpage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText etRegUsername, etRegEmail, etRegPhone, etRegPassword, etRegConfirmPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;
    private ImageButton btnBack;
    private CheckBox cbTerms;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private DatabaseReference counterReference;
    private DatabaseReference emailToClientKeyRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        counterReference = FirebaseDatabase.getInstance().getReference("userCounter");
        emailToClientKeyRef = FirebaseDatabase.getInstance().getReference("emailToClientKey");
        mAuth = FirebaseAuth.getInstance();
        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPhone = findViewById(R.id.etRegPhone);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        btnBack = findViewById(R.id.btnBack);
        cbTerms = findViewById(R.id.cbTerms);
        progressBar = findViewById(R.id.progressBar);
        etRegUsername.setHint("Tên người dùng");
        etRegEmail.setHint("Email");
        etRegPhone.setHint("Số điện thoại");
        etRegPassword.setHint("Mật khẩu");
        etRegConfirmPassword.setHint("Xác nhận mật khẩu");
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(view -> validateAndRegister());

        tvGoToLogin.setOnClickListener(view -> navigateToLogin());

        btnBack.setOnClickListener(v -> navigateToLogin());
    }

    private void validateAndRegister() {
        // Lấy dữ liệu từ form
        String username = etRegUsername.getText().toString().trim();
        String email = etRegEmail.getText().toString().trim();
        String phone = etRegPhone.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();
        String confirmPassword = etRegConfirmPassword.getText().toString().trim();

        // Reset errors
        etRegUsername.setError(null);
        etRegEmail.setError(null);
        etRegPhone.setError(null);
        etRegPassword.setError(null);
        etRegConfirmPassword.setError(null);

        // Validate inputs
        boolean hasError = false;

        if (username.isEmpty()) {
            etRegUsername.setError("Vui lòng nhập tên người dùng");
            hasError = true;
        }

        if (email.isEmpty()) {
            etRegEmail.setError("Vui lòng nhập email");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegEmail.setError("Email không hợp lệ");
            hasError = true;
        }

        if (phone.isEmpty()) {
            etRegPhone.setError("Vui lòng nhập số điện thoại");
            hasError = true;
        } else if (!isValidPhoneNumber(phone)) {
            etRegPhone.setError("Số điện thoại không hợp lệ (9-11 chữ số)");
            hasError = true;
        }

        if (password.isEmpty()) {
            etRegPassword.setError("Vui lòng nhập mật khẩu");
            hasError = true;
        } else if (password.length() < 6) {
            etRegPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            hasError = true;
        }

        if (confirmPassword.isEmpty()) {
            etRegConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            etRegConfirmPassword.setError("Mật khẩu không khớp");
            hasError = true;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý với Điều khoản dịch vụ", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        if (!hasError) {
            registerUser(username, email, phone, password);
        }
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{9,11}");
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

    private void registerUser(String username, String email, String phone, String password) {
        Log.d(TAG, "Bắt đầu đăng ký: " + email);
        Log.d(TAG, "Username: " + username);

        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối mạng. Vui lòng kiểm tra Wi-Fi hoặc dữ liệu di động!", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Lấy giá trị userCounter hiện tại và tăng lên 1
                            counterReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Long counter = dataSnapshot.getValue(Long.class);
                                    if (counter == null) {
                                        counter = 0L;
                                    }
                                    counter++;
                                    String clientKey = "client" + counter;
                                    counterReference.setValue(counter);
                                    User user = new User(username, email, phone, password);
                                    databaseReference.child(clientKey).setValue(user)
                                            .addOnCompleteListener(dbTask -> {
                                                if (dbTask.isSuccessful()) {
                                                    // Lưu ánh xạ email -> clientKey
                                                    String emailKey = email.replace(".", ",");
                                                    emailToClientKeyRef.child(emailKey).setValue(clientKey)
                                                            .addOnCompleteListener(emailTask -> {
                                                                progressBar.setVisibility(View.GONE);
                                                                if (emailTask.isSuccessful()) {
                                                                    Log.d(TAG, "Đăng ký thành công với key: " + clientKey);
                                                                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                                                    navigateToLogin();
                                                                } else {
                                                                    String errorMessage = emailTask.getException() != null ? emailTask.getException().getMessage() : "Lỗi khi lưu ánh xạ email";
                                                                    hideProgressAndShowError("Đăng ký thất bại: " + errorMessage);
                                                                }
                                                            });
                                                } else {
                                                    String errorMessage = dbTask.getException() != null ? dbTask.getException().getMessage() : "Lỗi khi lưu dữ liệu";
                                                    hideProgressAndShowError("Đăng ký thất bại: " + errorMessage);
                                                }
                                            });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    hideProgressAndShowError("Lỗi khi lấy userCounter: " + databaseError.getMessage());
                                }
                            });
                        } else {
                            hideProgressAndShowError("Lỗi: Không thể lấy userId");
                        }
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthException e) {
                            String errorCode = e.getErrorCode();
                            switch (errorCode) {
                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    hideProgressAndShowError("Email đã được sử dụng!");
                                    break;
                                case "ERROR_INVALID_EMAIL":
                                    hideProgressAndShowError("Email không hợp lệ!");
                                    break;
                                case "ERROR_WEAK_PASSWORD":
                                    hideProgressAndShowError("Mật khẩu quá yếu, cần ít nhất 6 ký tự!");
                                    break;
                                default:
                                    hideProgressAndShowError("Đăng ký thất bại: " + e.getMessage());
                                    break;
                            }
                        } catch (Exception e) {
                            hideProgressAndShowError("Đăng ký thất bại: " + e.getMessage());
                        }
                    }
                });
    }

    private void hideProgressAndShowError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        Log.e(TAG, errorMessage);
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public static class User {
        public String username;
        public String email;
        public String phone;
        public String password;
        public String fullName;
        public String dateOfBirth;
        public String gender;
        public String idNumber;
        public String address;
        public String street;
        public String ethnicity;
        public String nationality;
        public String job;

        public User() {
        }

        public User(String username, String email, String phone, String password) {
            this.username = username;
            this.email = email;
            this.phone = phone;
            this.password = password;
        }

        public User(String username, String email, String phone, String password,
                    String fullName, String dateOfBirth, String gender, String idNumber,
                    String address, String street, String ethnicity, String nationality, String job) {
            this.username = username;
            this.email = email;
            this.phone = phone;
            this.password = password;
            this.fullName = fullName;
            this.dateOfBirth = dateOfBirth;
            this.gender = gender;
            this.idNumber = idNumber;
            this.address = address;
            this.street = street;
            this.ethnicity = ethnicity;
            this.nationality = nationality;
            this.job = job;
        }
    }
}