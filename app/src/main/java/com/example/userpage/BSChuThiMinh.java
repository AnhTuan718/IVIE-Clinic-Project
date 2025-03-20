package com.example.userpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BSChuThiMinh extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.bschuthiminh);
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
        TextView directConsultation = findViewById(R.id.direct_consultation);

        // Set data
        // doctorImage.setImageResource(R.drawable.doctor_image);
        doctorName.setText("BS. Nguyễn Văn A");
        doctorRating.setText("4.5/5 (120 đánh giá)");
        doctorSpecialty.setText("Chuyên khoa: Nội tổng quát");
        doctorPrice.setText("Giá khám: 300.000 VNĐ");
        doctorWorkplace.setText("Nơi công tác: Bệnh viện Chợ Rẫy");
        doctorOnlineConsultation.setText("Nơi tư vấn online: Ứng dụng HealthCare");
        appointmentCount.setText("150");
        callCount.setText("80");
        consultationCount.setText("200");
        doctorInfoContent.setText("Bác sĩ Nguyễn Văn A tốt nghiệp Đại học Y Dược TP.HCM, có hơn 10 năm kinh nghiệm trong lĩnh vực nội khoa. Chuyên môn cao về các bệnh lý tiêu hóa và hô hấp.");
        clinicAddress.setText("Địa chỉ: 123 Đường Láng Hạ, Quận Đống Đa, Hà Nội");
        directConsultation.setText("Tư vấn trực tiếp:");

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("doctor_name");
        String specialty = intent.getStringExtra("doctor_specialty");

        // Handle expandable section
        doctorInfoTitle.setOnClickListener(v -> {
            if (doctorInfoContent.getVisibility() == View.GONE) {
                doctorInfoContent.setVisibility(View.VISIBLE);
                doctorInfoTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand, 0);
            } else {
                doctorInfoContent.setVisibility(View.GONE);
                doctorInfoTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand, 0);
                // Để xoay icon khi thu gọn, bạn có thể cần tạo một icon khác hoặc dùng animation
            }
        });

        // Xử lý nút Back
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Share
        ImageButton btnShare = findViewById(R.id.btn_share);
        btnShare.setOnClickListener(v -> {
            // Thêm logic chia sẻ ở đây
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareText = "Thông tin bác sĩ: " + doctorName.getText() + "\n" +
                    doctorSpecialty.getText() + "\n" +
                    doctorWorkplace.getText() + "\n" +
                    "Lượt hẹn khám: " + appointmentCount.getText();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
        });
    }
}
