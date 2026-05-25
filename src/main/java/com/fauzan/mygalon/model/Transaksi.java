package com.fauzan.mygalon.model;

public class Transaksi {
    private int idTransaksi;
    private int jumlahGalon;
    private int totalTransaksi;
    private String namaPembeli;
    private String namaKurir;
    private String tanggalTransaksi;
    private String metodePengiriman;
    private String alamatPengantaran;
    private String statusTransaksi;

//    construktor insert
    public Transaksi (
            String namaPembeli,
            int jumlahGalon,
            String namaKurir,
            String metodePengiriman,
            String alamatPengantaran,
            int totalTransaksi,
            String statusTransaksi
    ){
        this.namaPembeli = namaPembeli;
        this.jumlahGalon = jumlahGalon;
        this.totalTransaksi = totalTransaksi;
        this.namaKurir = namaKurir;
        this.metodePengiriman = metodePengiriman;
        this.alamatPengantaran = alamatPengantaran;
        this.statusTransaksi = statusTransaksi;
    }

//    construktor select
    public Transaksi (
            int idTransaksi,
            String namaPembeli,
            int jumlahGalon,
            String namaKurir,
            String metodePengiriman,
            String alamatPengantaran,
            String tanggalTransaksi,
            int totalTransaksi,
            String statusTransaksi
    ){
        this.idTransaksi = idTransaksi;
        this.namaPembeli = namaPembeli;
        this.jumlahGalon = jumlahGalon;
        this.tanggalTransaksi = tanggalTransaksi;
        this.totalTransaksi = totalTransaksi;
        this.namaKurir = namaKurir;
        this.metodePengiriman = metodePengiriman;
        this.alamatPengantaran = alamatPengantaran;
        this.statusTransaksi = statusTransaksi;
    }

//    Getter
    public int getIdTransaksi() {
        return idTransaksi;
    }

    public String getNamaPembeli() {
        return namaPembeli;
    }

    public String getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public int getTotalTransaksi() {
        return totalTransaksi;
    }

    public String getNamaKurir() {
        return namaKurir;
    }

    public String getMetodePengiriman() {
        return metodePengiriman;
    }

    public String getAlamatPengantaran() {
        return alamatPengantaran;
    }

    public String getStatusTransaksi() {
        return statusTransaksi;
    }

    public int getJumlahGalon() { return jumlahGalon;}
}
