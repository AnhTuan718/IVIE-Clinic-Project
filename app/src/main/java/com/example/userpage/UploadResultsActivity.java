package com.example.userpage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class UploadResultsActivity extends AppCompatActivity {

    private TextInputEditText dateInput;
    private AutoCompleteTextView hospitalInput;
    private TextInputEditText patientInput; // Đổi từ AutoCompleteTextView thành TextInputEditText
    private TextInputEditText serviceInput;
    private TextInputEditText resultsInput;
    private static final int FILE_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_results); // Correctly reference the layout file

        // Initialize views
        dateInput = findViewById(R.id.dateInput);
        hospitalInput = findViewById(R.id.hospitalInput);
        patientInput = findViewById(R.id.patientInput); // Vẫn dùng id cũ nhưng giờ là TextInputEditText
        serviceInput = findViewById(R.id.serviceInput);
        resultsInput = findViewById(R.id.resultsInput);

        // Back button
        ImageButton backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        // Set up date input
        if (dateInput != null) {
            dateInput.setOnClickListener(v -> showDatePicker());
        }

        // Set up hospital dropdown
        setupHospitalDropdown();

        // Upload file button
        MaterialButton uploadButton = findViewById(R.id.uploadButton);
        if (uploadButton != null) {
            uploadButton.setOnClickListener(v -> selectFile());
        }

        // Submit button
        MaterialButton submitButton = findViewById(R.id.submitButton);
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> submitForm());
        }
    }

    private void setupHospitalDropdown() {
        // Sample data - replace with your actual data source
        String[] hospitals = new String[] {
                "Bệnh viện Chợ Rẫy",
                "Bệnh viện Thống Nhất",
                "Phòng khám Đa khoa Phúc An",
                "Bệnh viện 115"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                hospitals
        );

        hospitalInput.setAdapter(adapter);
    }

    // Đã loại bỏ setupPatientDropdown() vì không còn là dropdown nữa

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateInput.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            Toast.makeText(this, "Đã chọn file: " + fileUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
        }
    }

    private void submitForm() {
        String date = dateInput.getText().toString().trim();
        String hospital = hospitalInput.getText().toString().trim();
        String patient = patientInput.getText().toString().trim();
        String service = serviceInput.getText().toString().trim();
        String results = resultsInput.getText().toString().trim();

        if (date.isEmpty() || hospital.isEmpty() || patient.isEmpty() || service.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ các trường bắt buộc", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Tải lên thành công!\nNgày: " + date + "\nBệnh viện: " + hospital +
                            "\nNgười khám: " + patient + "\nDịch vụ: " + service + "\nKết quả: " + results,
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }
}