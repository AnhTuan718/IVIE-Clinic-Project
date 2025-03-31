package com.example.userpage.Fragment;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.userpage.Adapter.ChuyenKhoaAdapter;
import com.example.userpage.Adapter.DoctorAdapter;
import com.example.userpage.Adapter.PhotoAdapter;
import com.example.userpage.Model.ChuyenKhoa;
import com.example.userpage.Model.Doctor;
import com.example.userpage.Model.Photo;
import com.example.userpage.R;
import com.example.userpage.RecyclerViewInterface;
import com.example.userpage.BSChuThiMinh;
import com.example.userpage.BSNguyenHoangGiang;
import com.example.userpage.Shopping.ProductDetailActivity;
import com.example.userpage.Shopping.ShoppingFragment;

import java.util.ArrayList;
import java.util.List;

public class TrangChuFragment extends Fragment implements RecyclerViewInterface {

    private ViewPager2 mviewPager2;
    private RecyclerView doctorRecyclerView;
    private RecyclerView chuyenKhoaRecyclerView;

    private Handler handler = new Handler(Looper.getMainLooper()); //Handler để quản lý slide
    private Runnable runnable; //thực hiện chuyển slide

    private static final int AUTO_SLIDE_DELAY = 3000; // Thời gian chờ giữa các slide (3 giây)
    EditText edtTimKiem;

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), BSChuThiMinh.class);
        Intent intent1 = new Intent(getActivity(), BSNguyenHoangGiang.class);
        startActivity(position == 0 ? intent : intent1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trang_chu, container, false);
        edtTimKiem = view.findViewById(R.id.edtTimKiem);
        mviewPager2 = view.findViewById(R.id.viewpager);

        //khoi tao photoAdapter
        PhotoAdapter photoAdapter = new PhotoAdapter(getActivity(), getListPhoto());
        mviewPager2.setAdapter(photoAdapter);

        setupAutoSlide();

        doctorRecyclerView = view.findViewById(R.id.doctorRecyclerView);
        chuyenKhoaRecyclerView = view.findViewById(R.id.chuyenKhoaRecyclerView);
        chuyenKhoaRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        List<ChuyenKhoa> chuyenKhoaList = new ArrayList<>();
        chuyenKhoaList.add(new ChuyenKhoa(R.drawable.stethoscope, "Khám sức khỏe tổng quát"));
        chuyenKhoaList.add(new ChuyenKhoa(R.drawable.homecare, "Khám tại nhà"));
        chuyenKhoaList.add(new ChuyenKhoa(R.drawable.patient, "Xét nghiệm cúm A,b"));
        chuyenKhoaList.add(new ChuyenKhoa(R.drawable.dna_strand, "Xét nghiệm Gene"));

        ChuyenKhoaAdapter ckadapter = new ChuyenKhoaAdapter(chuyenKhoaList);
        chuyenKhoaRecyclerView.setAdapter(ckadapter);

        List<Doctor> doctorList = new ArrayList<>();
        doctorList.add(new Doctor("BS. Chu Thị Minh", "Bệnh viện Chợ Rẫy", R.drawable.bs_chuthiminh));
        doctorList.add(new Doctor("BS. Nguyễn Hoàng Giang", "Bệnh viện Từ Dũ", R.drawable.bs_nguyenhoanggiang));
        doctorList.add(new Doctor("BS. Nguyễn Sỹ Đức", "Bệnh viện 115", R.drawable.bs_nguyensyduc));

        doctorRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        DoctorAdapter adapter = new DoctorAdapter(this, doctorList);
        doctorRecyclerView.setAdapter(adapter);
        CardView shoppingCard = view.findViewById(R.id.shopping_card);
        shoppingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShoppingFragment.class);
                startActivity(intent);
            }
        });
        //Click chuyen qua benh vien
        CardView hospitalCard = view.findViewById(R.id.hospital_card); // Đảm bảo thêm ID này trong XML
        hospitalCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BenhVien.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void setupAutoSlide() {
        runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = mviewPager2.getCurrentItem();
                int totalItem = mviewPager2.getAdapter().getItemCount();
                if (currentItem < totalItem - 1) {
                    mviewPager2.setCurrentItem(currentItem + 1);
                } else {
                    mviewPager2.setCurrentItem(0); //Quay lại slide đầu tiên
                }
                handler.postDelayed(this, AUTO_SLIDE_DELAY); //Lặp lại sau 3 giây
            }
        };
        handler.postDelayed(runnable, AUTO_SLIDE_DELAY);//Bắt đầu tự động chuyen slide
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Xóa runnable khi Fragment bị hủy để tránh memory Leak
        handler.removeCallbacks(runnable);
    }

    private List<Photo> getListPhoto() {
        List<Photo> list = new ArrayList<>();
        list.add(new Photo(R.drawable.quang_cao_ivie2));
        list.add(new Photo(R.drawable.quang_cao_ivie));
        return list;
    }
}