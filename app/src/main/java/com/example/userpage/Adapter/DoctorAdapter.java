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
import com.example.userpage.Model.Doctor;
import com.example.userpage.R;
import com.example.userpage.RecyclerViewInterface;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    private List<Doctor> doctorList;
    private final Context context;

    // Constructor
    public DoctorAdapter(Context context, RecyclerViewInterface recyclerViewInterface, List<Doctor> doctorList) {
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
        this.doctorList = doctorList;
    }

    // Cập nhật danh sách bác sĩ
    public void updateList(List<Doctor> newList) {
        this.doctorList = newList;
        notifyDataSetChanged();
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
        holder.doctorImage.setImageResource(doctor.getImageResId());
        holder.doctorName.setText(doctor.getName());
        holder.doctorWorkplace.setText(doctor.getWorkplace());

        // Xử lý sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (recyclerViewInterface != null) {
                recyclerViewInterface.onItemClick(position);
            }
        });

        // Xử lý sự kiện click nút "Tư vấn ngay"
        holder.consultButton.setOnClickListener(v ->
                Toast.makeText(context, "Tư vấn với " + doctor.getName(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return (doctorList != null) ? doctorList.size() : 0;
    }

    // ViewHolder cho từng bác sĩ
    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        ImageView doctorImage;
        TextView doctorName, doctorWorkplace;
        Button consultButton;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorImage = itemView.findViewById(R.id.doctorImage);
            doctorName = itemView.findViewById(R.id.doctorName);
            doctorWorkplace = itemView.findViewById(R.id.doctorWorkplace);
            consultButton = itemView.findViewById(R.id.consultButton);
        }
    }
}
