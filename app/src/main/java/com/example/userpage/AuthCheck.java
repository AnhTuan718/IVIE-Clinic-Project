package com.example.userpage;

import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;

import com.example.userpage.Database.LoginActivity; // Verify this package path is correct
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthCheck {  // Added explicit class declaration
    private Context context;  // Added Context field

    // Constructor to initialize context
    public AuthCheck(Context context) {
        this.context = context;
    }

    private void ensureUserIsAuthenticated() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User is not authenticated, redirect to login
            Intent intent = new Intent(context, LoginActivity.class);
            ContextCompat.startActivity(context, intent, null);
            // If this is an Activity, you can use finish() by casting context
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).finish();
            }
            return;
        }

        // User is authenticated, proceed with data access
        // ...
    }
}