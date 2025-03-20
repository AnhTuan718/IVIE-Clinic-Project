package com.example.userpage.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import com.example.userpage.AccountActivity;
import com.example.userpage.HealthActivity;
import com.example.userpage.LichKhamActivity;
import com.example.userpage.OrderHistoryActivity;
import com.example.userpage.PolicyActivity;
import com.example.userpage.R;
import com.example.userpage.SettingAplicationActivity;
import com.example.userpage.EditProfileActivity;
import com.example.userpage.databinding.FragmentUserProfileBinding;

public class UserProfileFragment extends Fragment {

    private FragmentUserProfileBinding binding;
    private ActivityResultLauncher<Intent> editProfileLauncher;
    private String currentImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Load avatar from SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        currentImageUri = prefs.getString("AVATAR_URI", null);
        if (currentImageUri != null) {
            try {
                Uri imageUri = Uri.parse(currentImageUri);
                binding.profileImage.setImageURI(imageUri);
            } catch (Exception e) {
                binding.profileImage.setImageResource(R.drawable.default_avatar);
            }
        }

        // Edit profile launcher
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        String fullName = result.getData().getStringExtra("FULL_NAME");
                        if (fullName != null) {
                            binding.tvProfileName.setText(fullName);
                        }
                        String imageUriStringResult = result.getData().getStringExtra("IMAGE_URI");
                        if (imageUriStringResult != null) {
                            currentImageUri = imageUriStringResult;
                            Uri imageUri = Uri.parse(currentImageUri);
                            binding.profileImage.setImageURI(imageUri);
                        }
                    }
                });

        binding.btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            editProfileLauncher.launch(intent);
        });

        setupClickListeners();
        return view;
    }

    private void setupClickListeners() {
        binding.layoutOffer.setOnClickListener(v -> showToast("Ưu đãi của tôi"));
        binding.layoutAppointment.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), LichKhamActivity.class)));
        binding.layoutOrders.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), OrderHistoryActivity.class)));
        binding.layoutHealthRecord.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HealthActivity.class);
            SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
            currentImageUri = prefs.getString("AVATAR_URI", null); // Cập nhật URI từ SharedPreferences
            if (currentImageUri != null) {
                intent.putExtra("IMAGE_URI", currentImageUri);
            }
            startActivity(intent);
        });
        binding.layoutAppSettings.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), SettingAplicationActivity.class)));
        binding.layoutAccount.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AccountActivity.class)));
        binding.layoutPolicy.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), PolicyActivity.class)));
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}