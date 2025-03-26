package com.example.userpage.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;

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
import java.util.ArrayList;
import java.util.List;

public class TrangChuFragment extends Fragment implements RecyclerViewInterface {

    private Handler handler;
    private int currentPage = 0;
    private static final int DELAY_TIME = 3000;
    private ViewPager2 mviewPager2;
    private RecyclerView doctorRecyclerView;
    private RecyclerView chuyenKhoaRecyclerView;
    DoctorAdapter adapter;
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
        PhotoAdapter photoAdapter = new PhotoAdapter(getActivity(), getListPhoto());
        mviewPager2.setAdapter(photoAdapter);

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
        //thiet lap chuc nang tim kiem

        return view;
    }



    private List<Photo> getListPhoto() {
        List<Photo> list = new ArrayList<>();
        list.add(new Photo(R.drawable.quang_cao_ivie2));
        list.add(new Photo(R.drawable.quang_cao_ivie));
        return list;
    }
}