package com.example.userpage.Chat;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    private SharedPreferences sharedPreferences;
    private String doctorName;

    private static final String PREFS_NAME = "ChatPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        messageRecyclerView = findViewById(R.id.messageRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);
        doctorNameTitle = findViewById(R.id.doctorNameTitle);
        doctorImage = findViewById(R.id.doctorImageChat);
        doctorName = getIntent().getStringExtra("DOCTOR_NAME");
        int doctorImageRes = getIntent().getIntExtra("DOCTOR_IMAGE", R.drawable.default_avatar);

        if (doctorName != null) {
            doctorNameTitle.setText(doctorName);
        }
        doctorImage.setImageResource(doctorImageRes);
        messages = loadMessages(doctorName);
        if (messages.isEmpty()) {
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            Message welcomeMessage = new Message("Xin chào, tôi có thể giúp gì cho bạn?", false, currentTime);
            messages.add(welcomeMessage);
        }

        messageAdapter = new MessageAdapter(messages);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageRecyclerView.setAdapter(messageAdapter);
        messageRecyclerView.scrollToPosition(messages.size() - 1);

        backButton.setOnClickListener(v -> finish());

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Add message to list
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            Message newMessage = new Message(messageText, true, currentTime);
            messages.add(newMessage);
            saveMessages(doctorName);

            // Update UI
            messageAdapter.notifyItemInserted(messages.size() - 1);
            messageRecyclerView.scrollToPosition(messages.size() - 1);

            messageInput.setText("");
        }
    }

    private void saveMessages(String doctorName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(messages);
        editor.putString("Messages_" + doctorName, json);
        editor.apply();
    }
    private List<Message> loadMessages(String doctorName) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Messages_" + doctorName, null);
        if (json == null) {
            return new ArrayList<>();
        }
        return gson.fromJson(json, new TypeToken<List<Message>>(){}.getType());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveMessages(doctorName);
    }
}