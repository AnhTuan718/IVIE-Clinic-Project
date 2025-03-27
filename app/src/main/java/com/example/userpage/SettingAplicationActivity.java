package com.example.userpage;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SettingAplicationActivity extends AppCompatActivity {

    private TextView tvCurrentLanguage;
    private String currentLanguage = "Tiếng Việt"; // Default language

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_aplication);

        // Initialize views
        tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage);
        ImageButton btnBack = findViewById(R.id.btnBack);
        LinearLayout languageOption = findViewById(R.id.languageOption);

        // Set current language
        tvCurrentLanguage.setText(currentLanguage);

        // Back button click
        btnBack.setOnClickListener(v -> finish());

        // Language selection click
        languageOption.setOnClickListener(v -> showLanguageDialog());
    }

    private void showLanguageDialog() {
        String[] languages = {"Tiếng Việt", "English"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ngôn ngữ / Select language");

        // Get current language index
        int selectedIndex = 0;
        if ("English".equals(currentLanguage)) {
            selectedIndex = 1;
        }

        builder.setSingleChoiceItems(languages, selectedIndex, (dialog, which) -> {
            currentLanguage = languages[which];
            tvCurrentLanguage.setText(currentLanguage);

            // Implement language change
            if ("English".equals(currentLanguage)) {
                setLocale("en");
            } else {
                setLocale("vi");
            }

            dialog.dismiss();
            Toast.makeText(this, "Ngôn ngữ: " + currentLanguage, Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Restart activity to apply changes
        recreate();
    }
}