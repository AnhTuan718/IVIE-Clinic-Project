package com.example.userpage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Centralized helper class for Firebase operations
 * Uses ONLY Firebase Realtime Database (not Firestore)
 */
public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_CLIENT_KEY = "clientKey";
    private static final String KEY_USER_ROLE = "userRole";

    // Singleton instance
    private static FirebaseHelper instance;

    // Firebase references
    private final FirebaseDatabase database;
    private final DatabaseReference usersRef;
    private final DatabaseReference doctorsRef;
    private final DatabaseReference appointmentsRef;
    private final DatabaseReference medicinesRef;
    private final DatabaseReference emailToClientKeyRef;

    // Authentication
    private final FirebaseAuth mAuth;
    private final SharedPreferences prefs;

    private FirebaseHelper(Context context) {
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        doctorsRef = database.getReference("doctors");
        appointmentsRef = database.getReference("appointments");
        medicinesRef = database.getReference("medicines");
        emailToClientKeyRef = database.getReference("emailToClientKey");

        mAuth = FirebaseAuth.getInstance();
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized FirebaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Get current user's client key from SharedPreferences
     */
    public String getCurrentClientKey() {
        return prefs.getString(KEY_CLIENT_KEY, null);
    }

    /**
     * Get current user's role from SharedPreferences
     */
    public String getCurrentUserRole() {
        return prefs.getString(KEY_USER_ROLE, null);
    }

    /**
     * Add a new doctor to the database
     */
    public void addDoctor(Doctor doctor, final DatabaseCallback<String> callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        String role = getCurrentUserRole();
        if (!"admin".equals(role)) {
            callback.onError("Chỉ admin mới có quyền thêm bác sĩ");
            return;
        }

        // Generate a new key for the doctor
        String doctorId = doctorsRef.push().getKey();
        if (doctorId == null) {
            callback.onError("Không thể tạo ID cho bác sĩ");
            return;
        }

        doctor.setUserId(doctorId);

        // Save the doctor data
        doctorsRef.child(doctorId).setValue(doctor)
                .addOnSuccessListener(aVoid -> callback.onSuccess(doctorId))
                .addOnFailureListener(e -> callback.onError("Lỗi khi thêm bác sĩ: " + e.getMessage()));
    }

    /**
     * Get all doctors
     */
    public void getAllDoctors(final DatabaseCallback<List<Doctor>> callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        doctorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Doctor> doctors = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Doctor doctor = snapshot.getValue(Doctor.class);
                        if (doctor != null) {
                            // Make sure the doctor has an ID
                            if (doctor.getUserId() == null) {
                                doctor.setUserId(snapshot.getKey());
                            }
                            doctors.add(doctor);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi khi phân tích dữ liệu bác sĩ: " + e.getMessage());
                    }
                }
                callback.onSuccess(doctors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Lỗi cơ sở dữ liệu: " + databaseError.getMessage());
                callback.onError("Không thể tải danh sách bác sĩ: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Get a specific doctor by ID
     */
    public void getDoctorById(String doctorId, final DatabaseCallback<Doctor> callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        doctorsRef.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Doctor doctor = dataSnapshot.getValue(Doctor.class);
                    if (doctor != null) {
                        if (doctor.getUserId() == null) {
                            doctor.setUserId(dataSnapshot.getKey());
                        }
                        callback.onSuccess(doctor);
                    } else {
                        callback.onError("Không thể phân tích dữ liệu bác sĩ");
                    }
                } else {
                    callback.onError("Không tìm thấy bác sĩ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError("Lỗi khi tải thông tin bác sĩ: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Update a doctor's information
     */
    public void updateDoctor(String doctorId, Doctor doctor, final DatabaseCallback<Void> callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        String role = getCurrentUserRole();
        String clientKey = getCurrentClientKey();

        if (role == null || clientKey == null) {
            callback.onError("Không tìm thấy vai trò hoặc clientKey của người dùng");
            return;
        }

        // Check if user has permission to update this doctor
        if ("admin".equals(role) || (doctorId.equals(clientKey) && "doctor".equals(role))) {
            doctorsRef.child(doctorId).setValue(doctor)
                    .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                    .addOnFailureListener(e -> callback.onError("Lỗi khi cập nhật thông tin bác sĩ: " + e.getMessage()));
        } else {
            callback.onError("Không có quyền: Chỉ admin hoặc chính bác sĩ đó mới có thể cập nhật thông tin");
        }
    }

    /**
     * Delete a doctor
     */
    public void deleteDoctor(String doctorId, final DatabaseCallback<Void> callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        String role = getCurrentUserRole();
        if (!"admin".equals(role)) {
            callback.onError("Chỉ admin mới có quyền xóa bác sĩ");
            return;
        }

        doctorsRef.child(doctorId).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError("Lỗi khi xóa bác sĩ: " + e.getMessage()));
    }

    /**
     * Add a new medicine
     */
    public void addMedicine(Medicine medicine, final DatabaseCallback<String> callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        String role = getCurrentUserRole();
        if (!"admin".equals(role)) {
            callback.onError("Chỉ admin mới có quyền thêm thuốc");
            return;
        }

        // Generate a new key for the medicine
        String medicineId = medicinesRef.push().getKey();
        if (medicineId == null) {
            callback.onError("Không thể tạo ID cho thuốc");
            return;
        }

        medicine.setId(medicineId);

        // Save the medicine data
        medicinesRef.child(medicineId).setValue(medicine)
                .addOnSuccessListener(aVoid -> callback.onSuccess(medicineId))
                .addOnFailureListener(e -> callback.onError("Lỗi khi thêm thuốc: " + e.getMessage()));
    }

    /**
     * Get all medicines
     */
    public void getAllMedicines(final DatabaseCallback<List<Medicine>> callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        medicinesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Medicine> medicines = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Medicine medicine = snapshot.getValue(Medicine.class);
                        if (medicine != null) {
                            if (medicine.getId() == null) {
                                medicine.setId(snapshot.getKey());
                            }
                            medicines.add(medicine);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi khi phân tích dữ liệu thuốc: " + e.getMessage());
                    }
                }
                callback.onSuccess(medicines);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError("Không thể tải danh sách thuốc: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Get a specific medicine by ID
     */
    public void getMedicineById(String medicineId, final DatabaseCallback<Medicine> callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        medicinesRef.child(medicineId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Medicine medicine = dataSnapshot.getValue(Medicine.class);
                    if (medicine != null) {
                        if (medicine.getId() == null) {
                            medicine.setId(dataSnapshot.getKey());
                        }
                        callback.onSuccess(medicine);
                    } else {
                        callback.onError("Không thể phân tích dữ liệu thuốc");
                    }
                } else {
                    callback.onError("Không tìm thấy thuốc");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError("Lỗi khi tải thông tin thuốc: " + databaseError.getMessage());
            }
        });
    }

    // Interface for callbacks
    public interface DatabaseCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    // Model classes
    public static class Doctor {
        private String userId;
        private String name;
        private String specialization;
        private String experience1;
        private String experience2;
        private String workplace;
        private String onlineConsultationPlace;
        private String clinicAddress;

        public Doctor() {
            // Required empty constructor for Firebase
        }

        public Doctor(String userId, String name, String specialization, String experience1,
                      String experience2, String workplace, String onlineConsultationPlace,
                      String clinicAddress) {
            this.userId = userId;
            this.name = name;
            this.specialization = specialization;
            this.experience1 = experience1;
            this.experience2 = experience2;
            this.workplace = workplace;
            this.onlineConsultationPlace = onlineConsultationPlace;
            this.clinicAddress = clinicAddress;
        }

        // Getters and setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSpecialization() { return specialization; }
        public void setSpecialization(String specialization) { this.specialization = specialization; }

        public String getExperience1() { return experience1; }
        public void setExperience1(String experience1) { this.experience1 = experience1; }

        public String getExperience2() { return experience2; }
        public void setExperience2(String experience2) { this.experience2 = experience2; }

        public String getWorkplace() { return workplace; }
        public void setWorkplace(String workplace) { this.workplace = workplace; }

        public String getOnlineConsultationPlace() { return onlineConsultationPlace; }
        public void setOnlineConsultationPlace(String onlineConsultationPlace) { this.onlineConsultationPlace = onlineConsultationPlace; }

        public String getClinicAddress() { return clinicAddress; }
        public void setClinicAddress(String clinicAddress) { this.clinicAddress = clinicAddress; }
    }

    public static class Medicine {
        private String id;
        private String name;
        private String description;

        public Medicine() {
            // Required empty constructor for Firebase
        }

        public Medicine(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public Medicine(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}