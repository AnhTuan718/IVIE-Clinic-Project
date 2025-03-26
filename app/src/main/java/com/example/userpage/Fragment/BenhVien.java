package com.example.userpage.Fragment;



import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userpage.Adapter.HospitalAdapter;
import com.example.userpage.Model.Hospital;
import com.example.userpage.R;

import java.util.ArrayList;
import java.util.List;

public class BenhVien extends AppCompatActivity implements OnHospitalClick {
    private RecyclerView recyclerView;
    private HospitalAdapter adapter;
    private List<Hospital> hospitalList;
    private List<Hospital> filteredHospitalList; // Danh sách đã lọc
    private EditText searchBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_benh_vien);
        searchBar = findViewById(R.id.search_bar);
        // Khởi tạo nút quay lại
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình trước đó
            }
        });

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.hospital_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Dữ liệu mẫu
        hospitalList = new ArrayList<>();
        hospitalList.add(new Hospital(R.drawable.trung_tam_chan_troi_moi,
                "Số 76 Phố An Dương", "Trung Tâm Chăm Sóc Giảm Nhẹ Chân Trời Mới - Cơ sở 1 "));
        hospitalList.add(new Hospital(R.drawable.logo_benh_vien_199,
                "Số 216 Nguyễn Công Trứ, An Hải Đông, Sơn Trà, Đà Nẵng", "Bệnh viện 199 - Đà Nẵng"));
        hospitalList.add(new Hospital(R.drawable.bs_nguyensyduc,
                "284 Cống Quỳnh, Quận 1, TP.HCM", "Bệnh viện Từ Dũ"));
        hospitalList.add(new Hospital(R.drawable.bs_chuthiminh,
                "zx", "Bệnh viện chợ rẫy"));
        hospitalList.add(new Hospital(R.drawable.bs_nguyenhoanggiang,
                "78 Giải Phóng, Đống Đa, Hà Nội", "Bệnh viện Bạch Mai"));
        hospitalList.add(new Hospital(R.drawable.bs_nguyensyduc,
                "284 Cống Quỳnh, Quận 1, TP.HCM", "Bệnh viện Từ Dũ"));
        hospitalList.add(new Hospital(R.drawable.bs_chuthiminh,
                "201B Nguyễn Chí Thanh, Quận 5, TP.HCM", "Bệnh viện chợ rẫy"));
        hospitalList.add(new Hospital(R.drawable.bs_nguyenhoanggiang,
                "78 Giải Phóng, Đống Đa, Hà Nội", "Bệnh viện Bạch Mai"));
        hospitalList.add(new Hospital(R.drawable.bs_nguyensyduc,
                "284 Cống Quỳnh, Quận 1, TP.HCM", "Bệnh viện Từ Dũ"));
        hospitalList.add(new Hospital(R.drawable.bs_chuthiminh,
                "201B Nguyễn Chí Thanh, Quận 5, TP.HCM", "Bệnh viện chợ rẫy"));
        hospitalList.add(new Hospital(R.drawable.bs_nguyenhoanggiang,
                "78 Giải Phóng, Đống Đa, Hà Nội", "Bệnh viện Bạch Mai"));
        hospitalList.add(new Hospital(R.drawable.bs_nguyensyduc,
                "284 Cống Quỳnh, Quận 1, TP.HCM", "Bệnh viện Từ Dũ"));
        // Khởi tạo danh sách đã lọc
        filteredHospitalList = new ArrayList<>(hospitalList);

        adapter = new HospitalAdapter(hospitalList);
        recyclerView.setAdapter(adapter);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString()); // Lọc danh sách khi text thay đổi
            }
        });
    }

    private void filter(String text) {
        filteredHospitalList.clear(); // Xóa danh sách đã lọc cũ
        if (text.isEmpty()) {
            filteredHospitalList.addAll(hospitalList); // Nếu không có text, hiển thị toàn bộ danh sách
        } else {
            text = text.toLowerCase(); // Chuyển về chữ thường để tìm kiếm không phân biệt hoa thường
            for (Hospital hospital : hospitalList) {
                if (hospital.getName().toLowerCase().contains(text) ||
                        hospital.getAddress().toLowerCase().contains(text)) {
                    filteredHospitalList.add(hospital); // Thêm bệnh viện khớp với từ khóa
                }
            }
        }
        adapter.notifyDataSetChanged(); // Cập nhật adapter
    }
    @Override
    public void onHospitalClick(Hospital hospital) {
        Intent intent = new Intent(this, HospitalDetailActivity.class);
        intent.putExtra("hospital_name", hospital.getName());
        intent.putExtra("hospital_address", hospital.getAddress());
        intent.putExtra("hospital_image", hospital.getImageResId());
        startActivity(intent);
    }
}