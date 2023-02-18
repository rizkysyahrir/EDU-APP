package com.ucweb.eduapp;

public class KategoriModel {
    private String kategoriID, kategoriNama, kategoriImage;

    public KategoriModel(String kategoriID, String kategoriNama, String kategoriImage) {
        this.kategoriID = kategoriID;
        this.kategoriNama = kategoriNama;
        this.kategoriImage = kategoriImage;
    }

    public KategoriModel(){}

    public String getKategoriID() {
        return kategoriID;
    }

    public void setKategoriID(String kategoriID) {
        this.kategoriID = kategoriID;
    }

    public String getKategoriNama() {
        return kategoriNama;
    }

    public void setKategoriNama(String kategoriNama) {
        this.kategoriNama = kategoriNama;
    }

    public String getKategoriImage() {
        return kategoriImage;
    }

    public void setKategoriImage(String kategoriImage) {
        this.kategoriImage = kategoriImage;
    }
}
