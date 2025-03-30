package com.example.userpage.Model;

public class Doctor {
    private String userId;
    private String name;
    private String specialization;
    private String experience1;
    private String experience2;
    private String workplace;
    private String onlineConsultationPlace;
    private String clinicAddress;
    private String email;
    private String imageUrl;

    public Doctor() {
        // Empty constructor needed for Firebase
    }

    public Doctor(String userId, String name, String specialization, String email) {
        this.userId = userId;
        this.name = name;
        this.specialization = specialization;
        this.email = email;
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

    // Getters and Setters
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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

}