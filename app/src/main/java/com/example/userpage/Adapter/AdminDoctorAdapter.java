package com.example.userpage.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.userpage.Model.Doctor;
import com.example.userpage.R;

import java.util.List;

public class AdminDoctorAdapter extends RecyclerView.Adapter<AdminDoctorAdapter.DoctorViewHolder> {

    private Context context;
    private List<Doctor> doctorList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AdminDoctorAdapter(Context context, List<Doctor> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);

        holder.doctorName.setText(doctor.getName()); // Sửa từ getFullName() thành getName()
        holder.doctorSpecialty.setText(doctor.getSpecialization()); // Sửa từ getSpecialty() thành getSpecialization()
        holder.doctorHospital.setText(doctor.getWorkplace()); // Sửa từ getHospital() thành getWorkplace()

        // Tải ảnh bác sĩ bằng Glide
        if (doctor.getImageUrl() != null && !doctor.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(doctor.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_info_details) // Sử dụng ảnh mặc định của Android
                    .error(android.R.drawable.ic_menu_info_details)
                    .into(holder.doctorImage);
        } else {
            holder.doctorImage.setImageResource(android.R.drawable.ic_menu_info_details);
        }
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public class DoctorViewHolder extends RecyclerView.ViewHolder {
        ImageView doctorImage;
        TextView doctorName, doctorSpecialty, doctorHospital;
        ImageButton editButton, deleteButton;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorImage = itemView.findViewById(R.id.doctor_image);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpecialty = itemView.findViewById(R.id.doctor_specialty);
            doctorHospital = itemView.findViewById(R.id.doctor_hospital);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

            editButton.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onEditClick(getAdapterPosition());
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
}