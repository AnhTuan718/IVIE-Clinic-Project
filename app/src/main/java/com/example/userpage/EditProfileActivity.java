package com.example.userpage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.userpage.databinding.ActivityEditProfileBinding;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 101;
    private ActivityEditProfileBinding binding;
    private Uri selectedImageUri;
    private Calendar calendar;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        String fullName = binding.edtFullName.getText().toString().trim();
        String phone = binding.edtPhoneNumber.getText().toString().trim();
        String dateOfBirth = binding.edtDob.getText().toString().trim();
        String address = binding.edtStreet.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty() || dateOfBirth.isEmpty() || address.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin");
        } else {
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
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}