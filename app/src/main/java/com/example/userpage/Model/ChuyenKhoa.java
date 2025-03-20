package com.example.userpage.Model;

public class ChuyenKhoa {

    private final int chuyenKhoaImage;
    private final String chuyenKhoaName;


    public ChuyenKhoa(int chuyenKhoaImage, String chuyenKhoaName) {
        this.chuyenKhoaImage = chuyenKhoaImage;
        this.chuyenKhoaName = chuyenKhoaName;
    }

    public int getChuyenKhoaImage() {
        return chuyenKhoaImage;
    }

    public String getChuyenKhoaName() {
        return chuyenKhoaName;
    }
}
