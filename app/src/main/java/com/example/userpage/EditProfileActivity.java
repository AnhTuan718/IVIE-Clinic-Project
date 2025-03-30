package com.example.userpage;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.userpage.Database.RegisterActivity;
import com.example.userpage.databinding.ActivityEditProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final String TAG = "EditProfileActivity";
    private ActivityEditProfileBinding binding;
    private Uri selectedImageUri;
    private Calendar calendar;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private DatabaseReference emailToClientKeyRef;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo Firebase Database
        emailToClientKeyRef = FirebaseDatabase.getInstance().getReference("emailToClientKey");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        binding.btnBack.setOnClickListener(v -> finish());

        calendar = Calendar.getInstance();

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        binding.imgAvatar.setImageURI(selectedImageUri);
                    } else {
                        showToast("Không chọn được ảnh!");
                    }
                });

        binding.imgAvatar.setOnClickListener(v -> checkStoragePermission());

        setupDatePicker();
        setupAutoCompleteTextView(binding.spinnerCountryCode, R.array.country_codes);
        setupAutoCompleteTextView(binding.spinnerEthnicity, R.array.ethnicity_list);
        setupAutoCompleteTextView(binding.spinnerNationality, R.array.nationality_list);
        setupAutoCompleteTextView(binding.spinnerJob, R.array.job_list);

        binding.genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                String gender = binding.radioMale.isChecked() ? "Nam" : "Nữ";
                showToast("Chọn giới tính: " + gender);
            }
        });

        binding.btnSubmit.setOnClickListener(v -> handleFormSubmit());

        // Load thông tin hiện tại của người dùng
        loadUserData();
    }

    private void loadUserData() {
        // Lấy email từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String email = prefs.getString("userEmail", null);

        if (email == null) {
            showToast("Không tìm thấy email. Vui lòng đăng nhập lại!");
            finish();
            return;
        }

        // Truy vấn emailToClientKey để tìm clientKey
        String emailKey = email.replace(".", ",");
        emailToClientKeyRef.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String clientKey = dataSnapshot.getValue(String.class);
                    if (clientKey != null) {
                        // Truy vấn users/clientX để lấy thông tin hiện tại
                        usersRef.child(clientKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot userSnapshot) {
                                if (userSnapshot.exists()) {
                                    RegisterActivity.User user = userSnapshot.getValue(RegisterActivity.User.class);
                                    if (user != null) {
                                        // Hiển thị thông tin hiện tại lên giao diện
                                        if (user.fullName != null) binding.edtFullName.setText(user.fullName);
                                        if (user.phone != null) binding.edtPhoneNumber.setText(user.phone);
                                        if (user.dateOfBirth != null) binding.edtDob.setText(user.dateOfBirth);
                                        if (user.gender != null) {
                                            if (user.gender.equals("Nam")) {
                                                binding.radioMale.setChecked(true);
                                            } else if (user.gender.equals("Nữ")) {
                                                binding.radioFemale.setChecked(true);
                                            }
                                        }
                                        if (user.idNumber != null) binding.edtIDNumber.setText(user.idNumber);
                                        if (user.address != null) binding.edtAddress.setText(user.address);
                                        if (user.street != null) binding.edtStreet.setText(user.street);
                                        if (user.ethnicity != null) binding.spinnerEthnicity.setText(user.ethnicity, false);
                                        if (user.nationality != null) binding.spinnerNationality.setText(user.nationality, false);
                                        if (user.job != null) binding.spinnerJob.setText(user.job, false);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                showToast("Lỗi khi tải thông tin: " + databaseError.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showToast("Lỗi khi tải thông tin: " + databaseError.getMessage());
            }
        });
    }

    private void checkStoragePermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showToast("Cần quyền truy cập ảnh để chọn avatar!");
            }
            ActivityCompat.requestPermissions(this, new String[]{permission}, STORAGE_PERMISSION_CODE);
        } else {
            openImagePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                showToast("Quyền bị từ chối vĩnh viễn. Vui lòng vào Cài đặt!");
                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.fromParts("package", getPackageName(), null)));
            } else {
                showToast("Quyền truy cập ảnh bị từ chối!");
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select a photo"));
    }

    private void setupDatePicker() {
        android.app.DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateDateInView();
        };
        binding.edtDob.setOnClickListener(v -> new android.app.DatePickerDialog(this,
                dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void updateDateInView() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        binding.edtDob.setText(sdf.format(calendar.getTime()));
    }

    private void setupAutoCompleteTextView(android.widget.AutoCompleteTextView view, int arrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayResId,
                android.R.layout.simple_dropdown_item_1line);
        view.setAdapter(adapter);
        view.setOnItemClickListener((parent, v, position, id) ->
                showToast("Chọn: " + parent.getItemAtPosition(position)));
    }

    private void handleFormSubmit() {
        // Lấy thông tin từ giao diện
        String fullName = binding.edtFullName.getText().toString().trim();
        String phone = binding.edtPhoneNumber.getText().toString().trim();
        String dateOfBirth = binding.edtDob.getText().toString().trim();
        String gender = binding.radioMale.isChecked() ? "Nam" : binding.radioFemale.isChecked() ? "Nữ" : "";
        String idNumber = binding.edtIDNumber.getText().toString().trim();
        String address = binding.edtAddress.getText().toString().trim();
        String street = binding.edtStreet.getText().toString().trim();
        String ethnicity = binding.spinnerEthnicity.getText().toString().trim();
        String nationality = binding.spinnerNationality.getText().toString().trim();
        String job = binding.spinnerJob.getText().toString().trim();

        // Kiểm tra các trường bắt buộc
        if (fullName.isEmpty() || phone.isEmpty() || dateOfBirth.isEmpty() || address.isEmpty() ||
                ethnicity.isEmpty() || nationality.isEmpty() || job.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ các thông tin bắt buộc (*)");
            return;
        }

        // Lấy email từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String email = prefs.getString("userEmail", null);

        if (email == null) {
            showToast("Không tìm thấy email. Vui lòng đăng nhập lại!");
            finish();
            return;
        }

        // Truy vấn emailToClientKey để tìm clientKey
        String emailKey = email.replace(".", ",");
        emailToClientKeyRef.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String clientKey = dataSnapshot.getValue(String.class);
                    if (clientKey != null) {
                        // Truy vấn thông tin hiện tại của người dùng để giữ nguyên các trường không thay đổi
                        usersRef.child(clientKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot userSnapshot) {
                                if (userSnapshot.exists()) {
                                    RegisterActivity.User existingUser = userSnapshot.getValue(RegisterActivity.User.class);
                                    if (existingUser != null) {
                                        // Tạo đối tượng User mới với thông tin đã chỉnh sửa
                                        RegisterActivity.User updatedUser = new RegisterActivity.User(
                                                existingUser.username, // Giữ nguyên username
                                                existingUser.email,    // Giữ nguyên email
                                                phone,                // Cập nhật số điện thoại
                                                existingUser.password, // Giữ nguyên password
                                                fullName,
                                                dateOfBirth,
                                                gender,
                                                idNumber,
                                                address,
                                                street,
                                                ethnicity,
                                                nationality,
                                                job
                                        );

                                        // Lưu thông tin cập nhật vào Realtime Database
                                        usersRef.child(clientKey).setValue(updatedUser)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        // Trả về kết quả cho UserProfileFragment
                                                        Intent resultIntent = new Intent();
                                                        resultIntent.putExtra("FULL_NAME", fullName);
                                                        if (selectedImageUri != null) {
                                                            resultIntent.putExtra("IMAGE_URI", selectedImageUri.toString());
                                                            getSharedPreferences("MyPrefs", MODE_PRIVATE).edit()
                                                                    .putString("AVATAR_URI", selectedImageUri.toString())
                                                                    .apply();
                                                        }
                                                        setResult(RESULT_OK, resultIntent);
                                                        showToast("Cập nhật thành công!");
                                                        finish();
                                                    } else {
                                                        showToast("Lỗi khi cập nhật: " + task.getException().getMessage());
                                                    }
                                                });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                showToast("Lỗi khi cập nhật: " + databaseError.getMessage());
                            }
                        });
                    }
                } else {
                    showToast("Không tìm thấy dữ liệu người dùng!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showToast("Lỗi: " + databaseError.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}