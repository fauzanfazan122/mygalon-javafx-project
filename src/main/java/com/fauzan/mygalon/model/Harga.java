package com.fauzan.mygalon.model;

public class Harga {
    private int idHarga;
    private int hargaGalon;
    private int hargaKirim;

    public Harga(int idHarga, int hargaGalon, int hargaKirim) {
        this.hargaGalon = hargaGalon;
        this.hargaKirim = hargaKirim;
        this.idHarga = idHarga;
    }

    public int getHargaGalon() {
        return hargaGalon;
    }

    public int getHargaKirim() {
        return hargaKirim;
    }

    public int getIdHarga(){
        return idHarga;
    }
}
