package com.example.userpage;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.userpage.databinding.ActivitySearchResultsBinding;

public class SearchResultsActivity extends AppCompatActivity {

    private ActivitySearchResultsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
    }

    private void setupUI() {
        setTabSelected(binding.cccdSearch, true);
        setTabSelected(binding.patientCodeSearch, false);

        String[] hospitals = {
                "Chọn Bệnh viện, Phòng khám",
                "Bệnh viện Chợ Rẫy",
                "Bệnh viện Bạch Mai",
                "Bệnh viện Đại học Y Dược TP.HCM",
                "Phòng khám Đa khoa Quốc tế",
                "Bệnh viện Nhi Trung ương"
        };
        ArrayAdapter<String> hospitalAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, hospitals);
        hospitalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.hospitalSpinner.setAdapter(hospitalAdapter);

        binding.cccdSearch.setOnClickListener(v -> {
            setTabSelected(binding.cccdSearch, true);
            setTabSelected(binding.patientCodeSearch, false);
            switchToSearchByCCCD();
        });

        binding.patientCodeSearch.setOnClickListener(v -> {
            setTabSelected(binding.patientCodeSearch, true);
            setTabSelected(binding.cccdSearch, false);
            switchToSearchByPatientCode();
        });

        binding.backButton.setOnClickListener(v -> finish());

        binding.searchButton.setOnClickListener(v -> performSearch());
    }

    private void setTabSelected(android.widget.LinearLayout tabLayout, boolean isSelected) {
        tabLayout.setSelected(isSelected);
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            tabLayout.getChildAt(i).setSelected(isSelected);
        }
    }

    private void switchToSearchByCCCD() {
        binding.cccdFieldLabel.setVisibility(View.VISIBLE);
        binding.patientCodeLabel.setVisibility(View.GONE);
        binding.patientCodeContainer.setVisibility(View.GONE);
    }

    private void switchToSearchByPatientCode() {
        binding.cccdFieldLabel.setVisibility(View.GONE);
        binding.patientCodeLabel.setVisibility(View.VISIBLE);
        binding.patientCodeContainer.setVisibility(View.VISIBLE);
    }

    private void performSearch() {
        String hospital = binding.hospitalSpinner.getSelectedItem().toString();
        String patient = binding.patientSpinner.getText().toString().trim();
        String searchType = binding.cccdSearch.isSelected() ? "CCCD" : "PatientCode";
        String searchValue = binding.cccdSearch.isSelected() ?
                binding.cccdFieldLabel.getText().toString() :
                binding.patientCodeInput.getText().toString().trim();

        if (hospital.equals("Chọn Bệnh viện, Phòng khám") || patient.isEmpty() || searchValue.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Đang tra cứu: " + searchType + " - " + searchValue + " tại " + hospital + " cho " + patient,
                    Toast.LENGTH_LONG).show();
            // Add your actual search logic here (e.g., API call or database query)
        }
    }
}
