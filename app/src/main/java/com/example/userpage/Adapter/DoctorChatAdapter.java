package com.example.userpage.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.userpage.Model.Doctor;
import com.example.userpage.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class DoctorChatAdapter extends RecyclerView.Adapter<DoctorChatAdapter.DoctorChatViewHolder> {

    private List<Doctor> doctorList;
    private OnDoctorClickListener listener;

    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }

    public DoctorChatAdapter(List<Doctor> doctorList, OnDoctorClickListener listener) {
        this.doctorList = doctorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor_chat, parent, false); // Đảm bảo dùng item_doctor_chat
        return new DoctorChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorChatViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.doctorImageChat.setImageResource(doctor.getImageResId());
        holder.doctorNameChat.setText(doctor.getName());
        holder.doctorWorkplaceChat.setText(doctor.getWorkplace());
        holder.lastMessageTime.setText(""); // Để trống hoặc thêm logic lấy thời gian sau

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDoctorClick(doctor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList != null ? doctorList.size() : 0;
    }

    public void updateList(List<Doctor> newList) {
        this.doctorList = newList;
        notifyDataSetChanged();
    }

    static class DoctorChatViewHolder extends RecyclerView.ViewHolder {
        CircleImageView doctorImageChat;
        TextView doctorNameChat;
        TextView doctorWorkplaceChat;
        TextView lastMessageTime;

        DoctorChatViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorImageChat = itemView.findViewById(R.id.doctorImageChat);
            doctorNameChat = itemView.findViewById(R.id.doctorNameChat);
            doctorWorkplaceChat = itemView.findViewById(R.id.doctorWorkplaceChat);
            lastMessageTime = itemView.findViewById(R.id.lastMessageTime);
        }
    }
}