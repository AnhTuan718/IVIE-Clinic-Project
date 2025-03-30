package com.example.userpage;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.userpage.Model.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private static final String TAG = "DatabaseAccess";
    private static DatabaseAccess instance;

    private FirebaseAuth auth;
    private DatabaseReference database;

    private DatabaseAccess() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized DatabaseAccess getInstance() {
        if (instance == null) {
            instance = new DatabaseAccess();
        }
        return instance;
    }

    public void getDoctors(final DatabaseCallback<List<Doctor>> callback) {
        // Kiểm tra xác thực
        if (auth.getCurrentUser() == null) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        // Truy cập dữ liệu
        database.child("doctors").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Doctor> doctors = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Doctor doctor = snapshot.getValue(Doctor.class);
                    if (doctor != null) {
                        doctors.add(doctor);
                    }
                }
                callback.onSuccess(doctors);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Lỗi: " + databaseError.getMessage());
                callback.onError(databaseError.getMessage());
            }
        });
    }

    // Interface cho callbacks
    public interface DatabaseCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}