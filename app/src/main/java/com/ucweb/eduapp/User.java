package com.ucweb.eduapp;

import android.net.Uri;

public class User {
    private String nama;
    private String email;

    public String getProfil() {
        return profil;
    }


    public void setProfil(String profil) {
        this.profil = profil;
    }


    private String profil;
    private String pass;
    private String refCode;
    private long coins;

    public User() {
    }

    public User(String nama, String email, String pass, String refCode) {
        this.nama = nama;
        this.email = email;
        this.pass = pass;
        this.refCode = refCode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }
}
