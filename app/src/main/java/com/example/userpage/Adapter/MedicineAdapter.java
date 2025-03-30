package com.example.userpage.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.userpage.Model.Medicine;
import com.example.userpage.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private Context context;
    private List<Medicine> medicineList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MedicineAdapter(Context context, List<Medicine> medicineList) {
        this.context = context;
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);

        holder.medicineName.setText(medicine.getName());
        holder.medicineDescription.setText(medicine.getDescription());

        // Format giá tiền
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.medicinePrice.setText(currencyFormat.format(medicine.getPrice()));

        // Hiển thị trạng thái
        if (medicine.isAvailable()) {
            holder.medicineStatus.setText("Còn hàng");
            holder.medicineStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.medicineStatus.setText("Hết hàng");
            holder.medicineStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        // Tải ảnh thuốc bằng Glide
        if (medicine.getImageUrl() != null && !medicine.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(medicine.getImageUrl())
                    .placeholder(R.drawable.ic_medicine_placeholder)
                    .error(R.drawable.ic_medicine_placeholder)
                    .into(holder.medicineImage);
        } else {
            holder.medicineImage.setImageResource(R.drawable.ic_medicine_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public class MedicineViewHolder extends RecyclerView.ViewHolder {
        ImageView medicineImage;
        TextView medicineName, medicineDescription, medicinePrice, medicineStatus;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineImage = itemView.findViewById(R.id.medicineImage);
            medicineName = itemView.findViewById(R.id.medicineName);
            medicineDescription = itemView.findViewById(R.id.medicineDescription);
            medicinePrice = itemView.findViewById(R.id.medicinePrice);
            medicineStatus = itemView.findViewById(R.id.medicineStatus);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}

