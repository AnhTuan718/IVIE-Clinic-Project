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
import com.example.userpage.EditProfileActivity;
import com.example.userpage.HealthActivity;
import com.example.userpage.LichKhamActivity;
import com.example.userpage.OrderHistoryActivity;
import com.example.userpage.PolicyActivity;
import com.example.userpage.R;
import com.example.userpage.SettingAplicationActivity;
import com.example.userpage.databinding.FragmentUserProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        // Load username from Realtime Database
        loadUsernameFromDatabase();

        // Edit profile launcher
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        // Lấy username thay vì fullName
                        String username = result.getData().getStringExtra("USERNAME");
                        if (username != null) {
                            binding.tvProfileName.setText(username); // Hiển thị username
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

    private void loadUsernameFromDatabase() {
        // Lấy email từ SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        String email = prefs.getString("userEmail", null);

        if (email == null) {
            Toast.makeText(getActivity(), "Không tìm thấy email. Vui lòng đăng nhập lại!", Toast.LENGTH_LONG).show();
            return;
        }

        // Truy vấn emailToClientKey để tìm clientKey
        DatabaseReference emailToClientKeyRef = FirebaseDatabase.getInstance().getReference("emailToClientKey");
        String emailKey = email.replace(".", ",");
        emailToClientKeyRef.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(getActivity(), "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_LONG).show();
                    return;
                }

                String clientKey = dataSnapshot.getValue(String.class);
                if (clientKey == null) {
                    Toast.makeText(getActivity(), "Lỗi: Không tìm thấy clientKey!", Toast.LENGTH_LONG).show();
                    return;
                }

                // Truy vấn users/clientX để lấy fullName
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(clientKey);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot userSnapshot) {
                        if (userSnapshot.exists()) {
                            String fullName = userSnapshot.child("fullName").getValue(String.class);
                            if (fullName != null) {
                                binding.tvProfileName.setText(fullName);
                            } else {
                                Toast.makeText(getActivity(), "Không tìm thấy Họ và Tên!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
            currentImageUri = prefs.getString("AVATAR_URI", null);
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