package com.example.userpage.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userpage.Fragment.OnHospitalClick;
import com.example.userpage.Model.Hospital;
import com.example.userpage.R;

import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {
    public HospitalAdapter(List<Hospital> hospitalList, OnHospitalClick listener) {
        this.hospitalList = hospitalList;
        this.listener = listener;
    }

    private List<Hospital> hospitalList;
    private OnHospitalClick listener;
    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hospital, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HospitalViewHolder holder, int position) {
        Hospital hospital = hospitalList.get(position);
        holder.hospitalName.setText(hospital.getName());
        holder.hospitalAddress.setText(hospital.getAddress());
        holder.hospitalImage.setImageResource(hospital.getImageResId());
        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHospitalClick(hospital); // Gọi callback khi click
            }
        });
    }

    @Override
    public int getItemCount() {
        return hospitalList.size();
    }

    public static class HospitalViewHolder extends RecyclerView.ViewHolder {
        ImageView hospitalImage;
        TextView hospitalName;
        TextView hospitalAddress;

        public HospitalViewHolder(View itemView) {
            super(itemView);
            hospitalImage = itemView.findViewById(R.id.hospital_image);
            hospitalName = itemView.findViewById(R.id.hospital_name);
            hospitalAddress = itemView.findViewById(R.id.hospital_address);
        }
    }
}
