package com.example.userpage.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userpage.Model.ChuyenKhoa;
import com.example.userpage.R;

import java.util.List;

public class ChuyenKhoaAdapter extends RecyclerView.Adapter<ChuyenKhoaAdapter.ChuyenKhoaViewHolder> {
    public ChuyenKhoaAdapter(List<ChuyenKhoa> chuyenKhoaList) {
        this.chuyenKhoaList = chuyenKhoaList;
    }

    private List<ChuyenKhoa> chuyenKhoaList;

    @NonNull
    @Override
    public ChuyenKhoaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chuyen_khoa, parent, false);
        return new ChuyenKhoaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChuyenKhoaAdapter.ChuyenKhoaViewHolder holder, int position) {
        ChuyenKhoa chuyenKhoa = chuyenKhoaList.get(position);
        holder.khoaImage.setImageResource(chuyenKhoa.getChuyenKhoaImage());
        holder.khoaName.setText(chuyenKhoa.getChuyenKhoaName());
    }

    @Override
    public int getItemCount() {
        return chuyenKhoaList.size();
    }

    public static class ChuyenKhoaViewHolder extends RecyclerView.ViewHolder {
        ImageView khoaImage;
        TextView khoaName;
        public ChuyenKhoaViewHolder(@NonNull View itemView) {
            super(itemView);
            khoaImage = itemView.findViewById(R.id.img_chuyenKhoa);
            khoaName = itemView.findViewById(R.id.txt_TenKhoa);

        }
    }
}
