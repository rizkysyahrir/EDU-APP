package com.ucweb.eduapp;

public class Pertanyaan {
    private String pertanyaan, opsi1, opsi2, opsi3, opsi4, jawaban;

    public Pertanyaan(){

    }

    public Pertanyaan(String pertanyaan, String opsi1, String opsi2, String opsi3, String opsi4, String jawaban) {
        this.pertanyaan = pertanyaan;
        this.opsi1 = opsi1;
        this.opsi2 = opsi2;
        this.opsi3 = opsi3;
        this.opsi4 = opsi4;
        this.jawaban = jawaban;
    }

    public String getPertanyaan() {
        return pertanyaan;
    }

    public void setPertanyaan(String pertanyaan) {
        this.pertanyaan = pertanyaan;
    }

    public String getOpsi1() {
        return opsi1;
    }

    public void setOpsi1(String opsi1) {
        this.opsi1 = opsi1;
    }

    public String getOpsi2() {
        return opsi2;
    }

    public void setOpsi2(String opsi2) {
        this.opsi2 = opsi2;
    }

    public String getOpsi3() {
        return opsi3;
    }

    public void setOpsi3(String opsi3) {
        this.opsi3 = opsi3;
    }

    public String getOpsi4() {
        return opsi4;
    }

    public void setOpsi4(String opsi4) {
        this.opsi4 = opsi4;
    }

    public String getJawaban() {
        return jawaban;
    }

    public void setJawaban(String jawaban) {
        this.jawaban = jawaban;
    }
}
