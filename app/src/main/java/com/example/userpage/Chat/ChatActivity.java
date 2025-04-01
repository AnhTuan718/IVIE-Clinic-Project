package com.example.userpage.Chat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.userpage.Adapter.MessageAdapter;
import com.example.userpage.Model.Message;
import com.example.userpage.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView messageRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageView backButton;
    private TextView doctorNameTitle;
    private ImageView doctorImage;
    private MessageAdapter messageAdapter;
    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize views
        messageRecyclerView = findViewById(R.id.messageRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);
        doctorNameTitle = findViewById(R.id.doctorNameTitle);
        doctorImage = findViewById(R.id.doctorImageChat);

        // Set doctor info from intent
        String doctorName = getIntent().getStringExtra("DOCTOR_NAME");
        String doctorWorkplace = getIntent().getStringExtra("DOCTOR_WORKPLACE");
        int doctorImageRes = getIntent().getIntExtra("DOCTOR_IMAGE", R.drawable.default_avatar);

        if (doctorName != null) {
            doctorNameTitle.setText(doctorName);
        }
        doctorImage.setImageResource(doctorImageRes);

        // Setup RecyclerView
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageRecyclerView.setAdapter(messageAdapter);

        // Thêm tin nhắn chào từ bác sĩ
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        Message welcomeMessage = new Message("Xin chào, tôi có thể giúp gì cho bạn?", false, currentTime);
        messages.add(welcomeMessage);
        messageAdapter.notifyItemInserted(messages.size() - 1);
        messageRecyclerView.scrollToPosition(messages.size() - 1);

        // Back button
        backButton.setOnClickListener(v -> finish());

        // Send button
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Add message to list
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            Message newMessage = new Message(messageText, true, currentTime);
            messages.add(newMessage);

            // Update UI
            messageAdapter.notifyItemInserted(messages.size() - 1);
            messageRecyclerView.scrollToPosition(messages.size() - 1);

            // Clear input
            messageInput.setText("");
        }
    }
}