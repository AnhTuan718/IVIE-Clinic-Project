package com.example.userpage.Fragment;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.userpage.R;


public class HospitalDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hospital_detail);
        // Lấy dữ liệu từ Intent
        String hospitalName = getIntent().getStringExtra("hospital_name");
        String hospitalAddress = getIntent().getStringExtra("hospital_address");
        int hospitalImage = getIntent().getIntExtra("hospital_image", 0);

        // Gán dữ liệu vào các view
        ImageView imageView = findViewById(R.id.detail_hospital_image);
        TextView nameTextView = findViewById(R.id.detail_hospital_name);
        TextView addressTextView = findViewById(R.id.detail_hospital_address);

        imageView.setImageResource(hospitalImage);
        nameTextView.setText(hospitalName);
        addressTextView.setText(hospitalAddress);
    }
}