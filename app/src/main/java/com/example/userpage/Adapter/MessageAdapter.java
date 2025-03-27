package com.example.userpage.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userpage.Model.Message;
import com.example.userpage.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;
    private String currentUserId;

    public MessageAdapter() {
        this.messageList = new ArrayList<>();
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        // Kiểm tra xem tin nhắn là của người dùng hiện tại hay người khác
        if (message.getSenderId().equals(currentUserId)) {
            // Tin nhắn của người dùng hiện tại (hiển thị bên phải)
            holder.layoutUserMessage.setVisibility(View.VISIBLE);
            holder.layoutOtherMessage.setVisibility(View.GONE);
            holder.textViewUserMessage.setText(message.getMessageText());
        } else {
            // Tin nhắn của người khác (hiển thị bên trái)
            holder.layoutUserMessage.setVisibility(View.GONE);
            holder.layoutOtherMessage.setVisibility(View.VISIBLE);
            holder.textViewOtherMessage.setText(message.getMessageText());
        }

        // Hiển thị thời gian
        holder.textViewTimestamp.setText(message.getFormattedTimestamp());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutUserMessage, layoutOtherMessage;
        TextView textViewUserMessage, textViewOtherMessage, textViewTimestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutUserMessage = itemView.findViewById(R.id.layout_user_message);
            layoutOtherMessage = itemView.findViewById(R.id.layout_other_message);
            textViewUserMessage = itemView.findViewById(R.id.text_view_user_message);
            textViewOtherMessage = itemView.findViewById(R.id.text_view_other_message);
            textViewTimestamp = itemView.findViewById(R.id.text_view_timestamp);
        }
    }
}