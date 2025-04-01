package com.example.userpage.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.userpage.Adapter.DoctorChatAdapter;
import com.example.userpage.Model.Doctor;
import com.example.userpage.R;
import java.util.ArrayList;
import java.util.List;

public class DoctorChatListFragment extends Fragment implements DoctorChatAdapter.OnDoctorClickListener {

    private RecyclerView recyclerView;
    private DoctorChatAdapter adapter;
    private List<Doctor> doctorList;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_chat_list, container, false);

        recyclerView = view.findViewById(R.id.doctorChatRecyclerView);
        searchView = view.findViewById(R.id.searchDoctors);

        if (recyclerView == null || searchView == null) {
            return view; // Tránh crash nếu view không tìm thấy
        }

        setupRecyclerView();
        setupSearchView();

        return view;
    }

    private void setupRecyclerView() {
        if (getContext() == null) return;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        doctorList = getDoctorList();
        adapter = new DoctorChatAdapter(doctorList, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterDoctors(newText);
                return true;
            }
        });
    }

    private void filterDoctors(String text) {
        List<Doctor> filteredList = new ArrayList<>();
        for (Doctor doctor : getDoctorList()) {
            if (doctor.getName().toLowerCase().contains(text.toLowerCase()) ||
                    doctor.getWorkplace().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(doctor);
            }
        }
        adapter.updateList(filteredList);
    }

    private List<Doctor> getDoctorList() {
        List<Doctor> list = new ArrayList<>();
        list.add(new Doctor("BS. Chu Thị Minh", "Bệnh viện Chợ Rẫy", R.drawable.bs_chuthiminh));
        list.add(new Doctor("BS. Nguyễn Hoàng Giang", "Bệnh viện Từ Dũ", R.drawable.bs_nguyenhoanggiang));
        list.add(new Doctor("BS. Nguyễn Sỹ Đức", "Bệnh viện 115", R.drawable.bs_nguyensyduc));
        list.add(new Doctor("BS. Lê Văn Vĩnh", "Bệnh viện Đại học Y Dược", R.drawable.bs_levanvinh));
        list.add(new Doctor("BS. Lê Thị Hương", "Bệnh viện Nhi Đồng", R.drawable.bs_lethihuong));
        list.add(new Doctor("BS. Hoàng Văn Khanh", "Bệnh viện Nam Học và Hiếm Muộn", R.drawable.bs_hoangvankhanh));
        return list;
    }

    @Override
    public void onDoctorClick(Doctor doctor) {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("DOCTOR_NAME", doctor.getName());
        intent.putExtra("DOCTOR_WORKPLACE", doctor.getWorkplace());
        intent.putExtra("DOCTOR_IMAGE", doctor.getImageResId());
        startActivity(intent);
    }
}