package com.example.userpage.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.userpage.NavigationActivity;
import com.example.userpage.R;

public class BeforeLoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_before_login, container, false);

        CardView loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            // Gọi phương thức navigateToLogin từ NavigationActivity
            if (getActivity() instanceof NavigationActivity) {
                ((NavigationActivity) getActivity()).navigateToLogin();
            }
        });

        return view;
    }
}