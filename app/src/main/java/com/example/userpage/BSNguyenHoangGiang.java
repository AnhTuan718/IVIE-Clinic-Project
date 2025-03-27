package com.example.userpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BSNguyenHoangGiang extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bs_nguyen_hoang_giang);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Doctor info setup
        ImageView doctorImage = findViewById(R.id.doctor_image);
        TextView doctorName = findViewById(R.id.doctor_name);
        TextView doctorRating = findViewById(R.id.doctor_rating);
        TextView doctorSpecialty = findViewById(R.id.doctor_specialty);
        TextView doctorPrice = findViewById(R.id.doctor_price);
        TextView doctorWorkplace = findViewById(R.id.doctor_workplace);
        TextView doctorOnlineConsultation = findViewById(R.id.doctor_online_consultation);
        TextView appointmentCount = findViewById(R.id.appointment_count);
        TextView callCount = findViewById(R.id.call_count);
        TextView consultationCount = findViewById(R.id.consultation_count);
        TextView doctorInfoTitle = findViewById(R.id.doctor_info_title);
        TextView doctorInfoContent = findViewById(R.id.doctor_info_content);
        TextView clinicAddress = findViewById(R.id.clinic_address);
        TextView onlineAddress = findViewById(R.id.online_address);
        TextView directConsultation = findViewById(R.id.direct_consultation);
        Button buttonChat = findViewById(R.id.button_chat);

        // Set data with string resources
        doctorName.setText("BS Nguyễn Hoàng Giang");
        doctorRating.setText("4.5/5 (100 đánh giá)");
        doctorSpecialty.setText(getString(R.string.doctor_specialty_label) + " Thần kinh");
        doctorPrice.setText(getString(R.string.doctor_price_format,
                getString(R.string.doctor_price_label), "800.000", getString(R.string.doctor_vnd_currency)));
        doctorWorkplace.setText(getString(R.string.doctor_workplace_label) + " Bệnh viện Tràng An");
        doctorOnlineConsultation.setText(getString(R.string.doctor_online_consultation_label) + " Bác sĩ ơi - Phòng khám O2O");
        appointmentCount.setText("150");
        callCount.setText("80");
        consultationCount.setText("200");
        doctorInfoContent.setText("Bác sĩ Nguyễn Hoàng Giang tốt nghiệp Đại học Y Hà Nội, có hơn 15 năm kinh nghiệm trong lĩnh vực thần kinh.");
        clinicAddress.setText(getString(R.string.address_label) + " 123 Đường Láng Hạ, Quận Đống Đa, Hà Nội");
        onlineAddress.setText("Phòng B19 - Tổ hợp Y Tế Chất lượng cao Mediplus, 99 Tân Mai, Hoàng Mai, Thành Phố Hà Nội");
        directConsultation.setText(getString(R.string.direct_consultation_label));

        // Nhận dữ liệu từ Intent (nếu có)
        Intent intent = getIntent();
        String name = intent.getStringExtra("doctor_name");
        String specialty = intent.getStringExtra("doctor_specialty");
        if (name != null) doctorName.setText(name);
        if (specialty != null) doctorSpecialty.setText(getString(R.string.doctor_specialty_label) + " " + specialty);

        // Handle expandable section
        doctorInfoTitle.setOnClickListener(v -> {
            if (doctorInfoContent.getVisibility() == View.GONE) {
                doctorInfoContent.setVisibility(View.VISIBLE);
                doctorInfoTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_collapse, 0);
            } else {
                doctorInfoContent.setVisibility(View.GONE);
                doctorInfoTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand, 0);
            }
        });

        // Xử lý nút Back
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Share
        ImageButton btnShare = findViewById(R.id.btn_share);
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareText = getString(R.string.doctor_info_title) + ": " + doctorName.getText() + "\n" +
                    doctorSpecialty.getText() + "\n" +
                    doctorWorkplace.getText() + "\n" +
                    getString(R.string.appointment_count_label) + ": " + appointmentCount.getText();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
        });

        // Xử lý nút Chat
        buttonChat.setOnClickListener(v -> {
            Intent chatIntent = new Intent(BSNguyenHoangGiang.this, ChatActivity.class);
            chatIntent.putExtra("doctor_name", doctorName.getText().toString());
            chatIntent.putExtra("doctor_id", "doctor_id_placeholder"); // Thay bằng ID thực tế của bác sĩ
            startActivity(chatIntent);
        });
    }
}