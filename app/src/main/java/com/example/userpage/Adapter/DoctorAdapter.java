package com.example.userpage.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.userpage.R;
import com.example.userpage.RecyclerViewInterface;
import com.example.userpage.Model.Doctor;
import java.util.List;


public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;

    private List<Doctor> doctorList;
    private Context context;


        // Constructor
    public DoctorAdapter(RecyclerViewInterface recyclerViewInterface, List<Doctor> doctorList) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.doctorList = doctorList;
    }

    // Thêm phương thức để cập nhật danh sách
    public void updateList(List<Doctor> newList) {
        this.doctorList = newList;
        notifyDataSetChanged();
    }

    // HospitalViewHolder
    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        ImageView doctorImage;
        TextView doctorName;
        TextView doctorWorkplace;
        Button consultButton;

        public DoctorViewHolder(@NonNull View itemView,RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            doctorImage = itemView.findViewById(R.id.doctorImage);
            doctorName = itemView.findViewById(R.id.doctorName);
            doctorWorkplace = itemView.findViewById(R.id.doctorWorkplace);
            consultButton = itemView.findViewById(R.id.consultButton);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.doctorImage.setImageResource(doctor.getImageResId());
        holder.doctorName.setText(doctor.getName());
        holder.doctorWorkplace.setText(doctor.getWorkplace());
        // xu ly searchView
        holder.itemView.setOnClickListener(v -> {
            if (recyclerViewInterface != null) {
                recyclerViewInterface.onItemClick(position);
            }
        });
        // Xử lý sự kiện click nút "Tư vấn ngay"
        holder.consultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Tư vấn với " + doctor.getName(), Toast.LENGTH_SHORT).show();
                // Thêm logic tư vấn thực tế tại đây nếu cần
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }
}
