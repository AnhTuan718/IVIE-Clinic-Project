package com.example.userpage;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.userpage.databinding.ActivityHealthBinding;

public class HealthActivity extends AppCompatActivity {

    private ActivityHealthBinding binding;
    private static final String TAG = "HealthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Fade animation for emptyIcon
        ObjectAnimator fadeAnimation = ObjectAnimator.ofFloat(binding.emptyIcon, "alpha", 0.6f, 1.0f);
        fadeAnimation.setDuration(1500);
        fadeAnimation.setRepeatCount(ValueAnimator.INFINITE);
        fadeAnimation.setRepeatMode(ValueAnimator.REVERSE);
        fadeAnimation.start();

        // Set user info
        binding.userName.setText("Nguyễn Văn A");
        binding.visitCount.setText("5 Lần khám");
        binding.memberCount.setText("2 Thành viên");

        // Load avatar
        loadProfileImage();

        // Upload button
        binding.uploadButton.setOnClickListener(v -> {
            startActivity(new Intent(this, UploadResultsActivity.class));
        });

        // View results button
        binding.viewResultsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchResultsActivity.class));
        });

        // Friends option
        binding.friendsOption.setOnClickListener(v -> {
            showToast("Chức năng đang được phát triển");
        });

        // Add member option
        binding.addMemberOption.setOnClickListener(v -> {
            showToast("Chức năng đang được phát triển");
        });

        // View results card
        binding.viewResultsCard.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchResultsActivity.class));
        });

        // Upload results card
        binding.uploadResultsCard.setOnClickListener(v -> {
            startActivity(new Intent(this, UploadResultsActivity.class));
        });

        // Back button
        binding.backButton.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Làm mới ảnh mỗi khi Activity quay lại foreground
        loadProfileImage();
    }

    private void loadProfileImage() {
        // Ưu tiên lấy từ Intent
        Intent intent = getIntent();
        String imageUriString = intent.getStringExtra("IMAGE_URI");

        // Nếu Intent không có dữ liệu, lấy từ SharedPreferences
        if (imageUriString == null) {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            imageUriString = prefs.getString("AVATAR_URI", null);
        }

        if (imageUriString != null) {
            try {
                Uri imageUri = Uri.parse(imageUriString);
                binding.profileImage.setImageURI(imageUri);
                Log.d(TAG, "Loaded image from URI: " + imageUriString);
            } catch (Exception e) {
                Log.e(TAG, "Error loading image: " + e.getMessage());
                binding.profileImage.setImageResource(R.drawable.default_avatar);
            }
        } else {
            Log.d(TAG, "No image URI found, using default avatar");
            binding.profileImage.setImageResource(R.drawable.default_avatar);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}