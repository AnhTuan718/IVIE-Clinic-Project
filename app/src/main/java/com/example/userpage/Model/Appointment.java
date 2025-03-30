package com.example.userpage.Model;

public class Appointment {
    private String id;
    private String patientName;
    private String date;
    private String time;
    private String reason;
    private String doctorId; // Add doctorId field

    public Appointment() {
        // Required empty constructor for Firebase
    }

    public Appointment(String id, String patientName, String date, String time, String reason, String doctorId) {
        this.id = id;
        this.patientName = patientName;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.doctorId = doctorId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
}