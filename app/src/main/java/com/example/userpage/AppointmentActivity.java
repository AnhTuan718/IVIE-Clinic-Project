package com.example.userpage;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.userpage.Model.Appointment;

import java.util.Calendar;

public class AppointmentActivity extends AppCompatActivity {

    private TextView tvDoctorName, tvSpecialty, tvHospital, tvPrice;
    private TextInputEditText etPatientName, etPatientPhone, etPatientDob, etSymptoms, etAppointmentDate, etAppointmentTime;
    private RadioGroup rgGender, rgPaymentMethod;
    private RadioButton rbMale, rbFemale, rbCash;
    private Button btnSubmit;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        // Ánh xạ các view
        tvDoctorName = findViewById(R.id.tv_doctor_name);
        tvSpecialty = findViewById(R.id.tv_specialty);
        tvHospital = findViewById(R.id.tv_hospital);
        tvPrice = findViewById(R.id.tv_price);

        etPatientName = findViewById(R.id.et_patient_name);
        etPatientPhone = findViewById(R.id.et_patient_phone);
        etPatientDob = findViewById(R.id.et_patient_dob);
        etSymptoms = findViewById(R.id.et_symptoms);
        etAppointmentDate = findViewById(R.id.et_appointment_date);
        etAppointmentTime = findViewById(R.id.et_appointment_time);

        rgGender = findViewById(R.id.rg_gender);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);

        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        rbCash = findViewById(R.id.rb_cash);

        btnSubmit = findViewById(R.id.btn_submit);
        btnBack = findViewById(R.id.btn_back);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String doctorName = extras.getString("doctor_name");
            String specialty = extras.getString("doctor_specialty");
            String hospital = extras.getString("doctor_hospital");
            String price = extras.getString("doctor_price");

            tvDoctorName.setText(doctorName != null ? doctorName : "Không xác định");
            tvSpecialty.setText("Chuyên khoa: " + (specialty != null ? specialty : "Không xác định"));
            tvHospital.setText("Nơi làm việc: " + (hospital != null ? hospital : "Không xác định"));
            tvPrice.setText("Giá khám: " + (price != null ? price + " VNĐ" : "Không xác định"));
        }

        btnBack.setOnClickListener(v -> finish());

        etPatientDob.setOnClickListener(v -> showDatePicker(etPatientDob));

        etAppointmentDate.setOnClickListener(v -> showDatePicker(etAppointmentDate));
        etAppointmentTime.setOnClickListener(v -> showTimePicker());

        btnSubmit.setOnClickListener(v -> submitAppointment());
    }

    private void showDatePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> editText.setText(String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1)),
                year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> etAppointmentTime.setText(String.format("%02d:%02d", hourOfDay, minute1)),
                hour, minute, true);
        timePickerDialog.show();
    }

    private void submitAppointment() {
        String patientName = etPatientName.getText().toString().trim();
        String patientPhone = etPatientPhone.getText().toString().trim();
        String patientDob = etPatientDob.getText().toString().trim();
        String symptoms = etSymptoms.getText().toString().trim();
        String appointmentDate = etAppointmentDate.getText().toString().trim();
        String appointmentTime = etAppointmentTime.getText().toString().trim();
        String gender = rbMale.isChecked() ? "Nam" : rbFemale.isChecked() ? "Nữ" : "";
        String paymentMethod = rbCash.isChecked() ? "Thanh toán tại quầy" : "";

        if (patientName.isEmpty() || patientPhone.isEmpty() || patientDob.isEmpty() ||
                symptoms.isEmpty() || appointmentDate.isEmpty() || appointmentTime.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!patientPhone.matches("\\d{10}")) {
            Toast.makeText(this, "Số điện thoại phải có 10 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("appointments");
        String appointmentId = ref.push().getKey();

        Appointment appointment = new Appointment(
                tvDoctorName.getText().toString(),
                "Chờ duyệt",
                appointmentDate,
                appointmentTime,
                symptoms,
                patientName,
                patientPhone,
                patientDob,
                gender,
                paymentMethod
        );

        if (appointmentId != null) {
            ref.child(appointmentId).setValue(appointment).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(AppointmentActivity.this, LichKhamActivity.class);
                    intent.putExtra("appointment_status", "Chờ duyệt");
                    intent.putExtra("appointment_id", appointmentId); // Thêm ID để có thể tìm nhanh
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    Toast.makeText(this, "Đặt lịch thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}