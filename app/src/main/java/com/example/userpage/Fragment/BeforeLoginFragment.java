package com.example.userpage.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.userpage.Database.LoginActivity;
import com.example.userpage.NavigationActivity;
import com.example.userpage.R;

public class BeforeLoginFragment extends Fragment {
    private static final String TAG = "BeforeLoginFragment";
    private Handler mainHandler;
    private boolean isNavigating = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_before_login, container, false);

        try {
            CardView loginButton = view.findViewById(R.id.loginButton);
            loginButton.setOnClickListener(v -> {
                if (!isNavigating) {
                    isNavigating = true;
                    navigateToLogin();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: " + e.getMessage());
        }

        return view;
    }

    private void navigateToLogin() {
        Log.d(TAG, "Starting navigation to LoginActivity");
        try {
            if (getActivity() == null || !isAdded() || getActivity().isFinishing()) {
                Log.e(TAG, "Fragment or Activity is not in a valid state");
                showError("Không thể thực hiện thao tác này");
                isNavigating = false;
                return;
            }

            if (getActivity() instanceof NavigationActivity) {
                ((NavigationActivity) getActivity()).navigateToLogin();
            } else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            Log.d(TAG, "Navigation to LoginActivity completed");
            isNavigating = false;
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to login: " + e.getMessage());
            showError("Có lỗi xảy ra, vui lòng thử lại");
            isNavigating = false;
        }
    }

    private void showError(String message) {
        if (getContext() != null && isAdded()) {
            mainHandler.post(() -> 
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show()
            );
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
    }
}