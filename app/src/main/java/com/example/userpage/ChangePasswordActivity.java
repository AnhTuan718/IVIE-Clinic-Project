package com.example.userpage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private boolean isCurrentPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private DatabaseReference emailToClientKeyRef;
    private String clientKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        emailToClientKeyRef = FirebaseDatabase.getInstance().getReference("emailToClientKey");

        // Check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Get clientKey from emailToClientKey
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String email = prefs.getString("userEmail", null);
        if (email == null) {
            Toast.makeText(this, "Không tìm thấy email. Vui lòng đăng nhập lại!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String emailKey = email.replace(".", ",");
        emailToClientKeyRef.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    clientKey = dataSnapshot.getValue(String.class);
                    if (clientKey == null) {
                        Toast.makeText(ChangePasswordActivity.this, "Lỗi: Không tìm thấy clientKey!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChangePasswordActivity.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });

        // Initialize views
        ImageButton btnBack = findViewById(R.id.btnBack);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        // Set up password visibility toggle
        setupPasswordToggle();

        // Back button click
        btnBack.setOnClickListener(v -> finish());

        // Submit button click
        btnSubmit.setOnClickListener(v -> {
            String currentPassword = etCurrentPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

            if (validateInput(currentPassword, newPassword, confirmNewPassword)) {
                changePassword(currentPassword, newPassword);
            }
        });
    }

    private void setupPasswordToggle() {
        View.OnTouchListener passwordToggleListener = (v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                EditText editText = (EditText) v;
                if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width() - editText.getPaddingRight())) {
                    String fieldType = "";
                    if (editText == etCurrentPassword) {
                        fieldType = "current";
                    } else if (editText == etNewPassword) {
                        fieldType = "new";
                    } else if (editText == etConfirmNewPassword) {
                        fieldType = "confirm";
                    }
                    togglePasswordVisibility(editText, fieldType);
                    return true;
                }
            }
            return false;
        };

        etCurrentPassword.setOnTouchListener(passwordToggleListener);
        etNewPassword.setOnTouchListener(passwordToggleListener);
        etConfirmNewPassword.setOnTouchListener(passwordToggleListener);
    }

    private void togglePasswordVisibility(EditText editText, String fieldType) {
        int cursorPosition = editText.getSelectionStart();

        switch (fieldType) {
            case "current":
                if (isCurrentPasswordVisible) {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
                    isCurrentPasswordVisible = false;
                } else {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0);
                    isCurrentPasswordVisible = true;
                }
                break;
            case "new":
                if (isNewPasswordVisible) {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
                    isNewPasswordVisible = false;
                } else {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0);
                    isNewPasswordVisible = true;
                }
                break;
            case "confirm":
                if (isConfirmPasswordVisible) {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
                    isConfirmPasswordVisible = false;
                } else {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0);
                    isConfirmPasswordVisible = true;
                }
                break;
        }

        editText.setSelection(cursorPosition);
    }

    private boolean validateInput(String currentPassword, String newPassword, String confirmNewPassword) {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void changePassword(String currentPassword, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy người dùng", Toast.LENGTH_LONG).show();
            return;
        }

        // Re-authenticate the user
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update password in Firebase Authentication
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        // Update password in Realtime Database
                                        updatePasswordInDatabase(newPassword);
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "Lỗi: " + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Mật khẩu cũ không đúng", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updatePasswordInDatabase(String newPassword) {
        if (clientKey == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy clientKey!", Toast.LENGTH_LONG).show();
            return;
        }

        userRef.child(clientKey).child("password").setValue(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChangePasswordActivity.this, "Mật khẩu đã được đổi thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Lỗi cập nhật mật khẩu trong cơ sở dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}