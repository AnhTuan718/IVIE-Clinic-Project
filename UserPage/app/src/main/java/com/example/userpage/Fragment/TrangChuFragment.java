import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.userpage.Activity.NavigationActivity;

public class TrangChuFragment extends Fragment {

    private static final String TAG = "TrangChuFragment";
    private FirebaseDatabase.Reference doctorsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trang_chu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeFirebase();
    }

    private void initializeFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            doctorsRef = FirebaseDatabase.getInstance().getReference("doctors");
            loadDoctors();
        } else {
            // Chưa đăng nhập
            if (getActivity() instanceof NavigationActivity) {
                ((NavigationActivity) getActivity()).navigateToLogin();
            }
        }
    }

    private void loadDoctors() {
        if (doctorsRef == null) {
            Log.e(TAG, "doctorsRef is null");
            return;
        }

        doctorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Xử lý dữ liệu
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), 
                            "Bạn không có quyền truy cập dữ liệu này", 
                            Toast.LENGTH_SHORT).show();
                        if (getActivity() instanceof NavigationActivity) {
                            ((NavigationActivity) getActivity()).navigateToLogin();
                        }
                    }
                }
            }
        });
    }
} 