package com.example.userpage.Model;

public class Appointment {
    private String id; // Thêm trường id
    private String doctorName;
    private String status;
    private String date;
    private String time;
    private String symptoms;
    private String patientName;
    private String patientPhone;
    private String patientDob;
    private String gender;
    private String paymentMethod;

    public Appointment() {}

    public Appointment(String doctorName, String status, String date, String time, String symptoms,
                       String patientName, String patientPhone, String patientDob, String gender, String paymentMethod) {
        this.doctorName = doctorName;
        this.status = status;
        this.date = date;
        this.time = time;
        this.symptoms = symptoms;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.patientDob = patientDob;
        this.gender = gender;
        this.paymentMethod = paymentMethod;
    }

    // Getters và Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; } // Thêm setter cho id
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }
    public String getPatientDob() { return patientDob; }
    public void setPatientDob(String patientDob) { this.patientDob = patientDob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}