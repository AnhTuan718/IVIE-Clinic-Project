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
        hospitalList.add(new Hospital(R.drawable.logo_trung_tam_chan_troi_moi,
                "Số 76 Phố An Dương", "Trung Tâm Chăm Sóc Giảm Nhẹ Chân Trời Mới - Cơ sở 1 "));
        hospitalList.add(new Hospital(R.drawable.logo_benh_vien_199,
                "Số 216 Nguyễn Công Trứ, An Hải Đông, Sơn Trà, Đà Nẵng", "Bệnh viện 199 - Đà Nẵng"));
        hospitalList.add(new Hospital(R.drawable.logo_binh_an,
                "123 Đường Lê Lợi, Quận 1, TP. Hồ Chí Minh", "Bệnh viện Đa khoa Bình An"));
        hospitalList.add(new Hospital(R.drawable.logo_phong_kham_da_khoa_hung_thinh,
                "456 Đường Hoàng Diệu, Quận 7, TP. Hồ Chí Minh", "Phòng khám Đa khoa Hưng Thịnh"));
        hospitalList.add(new Hospital(R.drawable.logo_benh_vien_qt_an_phu,
                "789 Đường Nguyễn Trãi, Quận 5, TP. Hồ Chí Minh", "Bệnh viện Quốc tế An Phú"));
        hospitalList.add(new Hospital(R.drawable.logo_bv_tu_du,
                "284 Cống Quỳnh, Quận 1, TP.HCM", "Bệnh viện Từ Dũ"));
        hospitalList.add(new Hospital(R.drawable.logo_pk_y_duc,
                "321 Đường Võ Văn Kiệt, Quận Bình Thạnh, TP. Hồ Chí Minh", "Phòng khám Y Đức"));
        hospitalList.add(new Hospital(R.drawable.logo_bv_da_khoa_hoang_gia,
                " 567 Đường Trần Hưng Đạo, Quận 3, TP. Hồ Chí Minh", "Bệnh viện Đa khoa Hoàng Gia"));
        hospitalList.add(new Hospital(R.drawable.logo_pk_chuyen_khoa_nv,
                "890 Đường Phan Xích Long, Quận Phú Nhuận, TP. Hồ Chí Minh", "Phòng khám Chuyên khoa Nam Việt"));
        hospitalList.add(new Hospital(R.drawable.logo_bv_qt_viet_my,
                "112 Đường Lý Thường Kiệt, Quận Tân Bình, TP. Hồ Chí Minh", "Bệnh viện Quốc tế Việt Mỹ"));
        hospitalList.add(new Hospital(R.drawable.logo_pkdk_tam_an,
                "789 Đường Lê Văn Lương, Quận 7, TP. Hồ Chí Minh", "Phòng khám Đa khoa Tâm An"));
        hospitalList.add(new Hospital(R.drawable.logo_bv_dong_a,
                "456 Đường Quang Trung, Quận Gò Vấp, TP. Hồ Chí Minh", "Bệnh viện Chấn thương Chỉnh hình Đông Á"));
        // Khởi tạo danh sách đã lọc
        filteredHospitalList = new ArrayList<>(hospitalList);

        adapter = new HospitalAdapter(filteredHospitalList,this);
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