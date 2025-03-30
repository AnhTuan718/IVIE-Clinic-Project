package com.example.userpage.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.userpage.Model.Doctor;
import com.example.userpage.R;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private Context context;
    private List<Doctor> doctorList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position); // Click trên toàn bộ item
        void onConsultClick(int position); // Click trên nút "Tư vấn ngay"
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public DoctorAdapter(Context context, List<Doctor> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);

        holder.doctorName.setText(doctor.getName());
        holder.doctorWorkplace.setText(doctor.getWorkplace());
        holder.doctorSpecialization.setText(doctor.getSpecialization());

        // Tải ảnh bác sĩ bằng Glide
        if (doctor.getImageUrl() != null && !doctor.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(doctor.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_info_details)
                    .error(android.R.drawable.ic_menu_info_details)
                    .into(holder.doctorImage);
        } else {
            holder.doctorImage.setImageResource(android.R.drawable.ic_menu_info_details);
        }

        // Xử lý click trên nút "Tư vấn ngay"
        holder.consultButton.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onConsultClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public class DoctorViewHolder extends RecyclerView.ViewHolder {
        ImageView doctorImage;
        TextView doctorName, doctorWorkplace, doctorSpecialization;
        Button consultButton;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorImage = itemView.findViewById(R.id.doctorImage);
            doctorName = itemView.findViewById(R.id.doctorName);
            doctorWorkplace = itemView.findViewById(R.id.doctorWorkplace);
            doctorSpecialization = itemView.findViewById(R.id.doctorSpecialization);
            consultButton = itemView.findViewById(R.id.consultButton);

            // Xử lý click trên toàn bộ item
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}