package com.example.userpage;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private boolean isCurrentPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

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
                Toast.makeText(this, "Mật khẩu đã được đổi thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupPasswordToggle() {
        // Tạo một listener chung cho tất cả EditText
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

        // Áp dụng listener cho từng EditText
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

        editText.setSelection(cursorPosition); // Keep cursor position
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
}
