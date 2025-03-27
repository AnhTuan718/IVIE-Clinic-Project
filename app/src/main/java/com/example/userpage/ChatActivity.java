package com.example.userpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userpage.Adapter.MessageAdapter;
import com.example.userpage.Model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton btnBack;
    private ImageView doctorImage;
    private TextView doctorName;
    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private Button buttonSend;

    private FirebaseAuth mAuth;
    private DatabaseReference chatRef;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private String currentUserId;
    private String doctorId; // ID của bác sĩ (có thể là userId của bác sĩ)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Khởi tạo các thành phần giao diện
        toolbar = findViewById(R.id.toolbar);
        btnBack = findViewById(R.id.btn_back);
        doctorImage = findViewById(R.id.doctor_image);
        doctorName = findViewById(R.id.doctor_name);
        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        editTextMessage = findViewById(R.id.edit_text_message);
        buttonSend = findViewById(R.id.button_send);

        setSupportActionBar(toolbar);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (currentUserId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Nhận thông tin bác sĩ từ Intent
        Intent intent = getIntent();
        String doctorNameText = intent.getStringExtra("doctor_name");
        doctorId = intent.getStringExtra("doctor_id"); // Giả sử bạn truyền doctorId từ activity trước
        if (doctorId == null) {
            doctorId = "doctor_id_placeholder"; // Thay bằng ID thực tế của bác sĩ
        }
        doctorName.setText(doctorNameText != null ? doctorNameText : "BS Nguyễn Hoàng Giang");

        // Thiết lập RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter();
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);
        messageAdapter.setMessageList(messageList);

        // Thiết lập đường dẫn chat trong Realtime Database
        // Mỗi cuộc trò chuyện sẽ được lưu trong node "chats/<chatId>/messages"
        String chatId = generateChatId(currentUserId, doctorId);
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages");

        // Lắng nghe tin nhắn mới
        loadMessages();

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Gửi
        buttonSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                editTextMessage.setText("");
            } else {
                Toast.makeText(ChatActivity.this, "Vui lòng nhập tin nhắn!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generateChatId(String userId1, String userId2) {
        // Tạo chatId duy nhất bằng cách sắp xếp userId1 và userId2 theo thứ tự
        if (userId1.compareTo(userId2) < 0) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    private void sendMessage(String messageText) {
        String messageId = chatRef.push().getKey();
        if (messageId == null) return;

        Message message = new Message(currentUserId, messageText, System.currentTimeMillis());
        chatRef.child(messageId).setValue(message)
                .addOnSuccessListener(aVoid -> {
                    // Tin nhắn đã được gửi thành công
                    recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatActivity.this, "Lỗi gửi tin nhắn: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void loadMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                messageAdapter.setMessageList(messageList);
                recyclerViewMessages.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Lỗi tải tin nhắn: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}