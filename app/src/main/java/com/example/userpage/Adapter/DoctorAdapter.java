package com.example.userpage.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.userpage.Chat.ChatActivity;
import com.example.userpage.Model.Doctor;
import com.example.userpage.R;
import com.example.userpage.RecyclerViewInterface;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private List<Doctor> doctorList;
    private Context context;

    public DoctorAdapter(RecyclerViewInterface recyclerViewInterface, List<Doctor> doctorList) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.doctorList = doctorList;
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

        // Xử lý sự kiện nhấn nút "Tư vấn ngay"
        holder.consultButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("DOCTOR_NAME", doctor.getName());
            intent.putExtra("DOCTOR_WORKPLACE", doctor.getWorkplace());
            intent.putExtra("DOCTOR_IMAGE", doctor.getImageResId());
            context.startActivity(intent);
        });

        // Xử lý sự kiện nhấn vào toàn bộ item (nếu cần)
        holder.itemView.setOnClickListener(v -> {
            if (recyclerViewInterface != null) {
                recyclerViewInterface.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public void updateList(List<Doctor> newList) {
        this.doctorList = newList;
        notifyDataSetChanged();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        ImageView doctorImage;
        TextView doctorName;
        TextView doctorWorkplace;
        Button consultButton;

        DoctorViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            doctorImage = itemView.findViewById(R.id.doctorImage);
            doctorName = itemView.findViewById(R.id.doctorName);
            doctorWorkplace = itemView.findViewById(R.id.doctorWorkplace);
            consultButton = itemView.findViewById(R.id.consultButton);
        }
    }
}