package com.example.userpage.Doctor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userpage.Model.Appointment;
import com.example.userpage.R;

import java.util.List;

public class DoctorAppointmentAdapter extends RecyclerView.Adapter<DoctorAppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;

    public DoctorAppointmentAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.tvPatientName.setText("Bệnh nhân: " + (appointment.getPatientName() != null ? appointment.getPatientName() : "Không xác định"));
        holder.tvDateTime.setText("Thời gian: " + (appointment.getDate() != null ? appointment.getDate() : "") + " " + (appointment.getTime() != null ? appointment.getTime() : ""));
        holder.tvReason.setText("Lý do: " + (appointment.getReason() != null ? appointment.getReason() : "Không có lý do"));
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvDateTime, tvReason;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvReason = itemView.findViewById(R.id.tvReason);
        }
    }
}