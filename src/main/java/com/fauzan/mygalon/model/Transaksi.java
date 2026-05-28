package com.fauzan.mygalon.model;

public class Transaksi {
    private int idTransaksi;
    private int jumlahGalon;
    private int hargaGalon;
    private int hargaKirim;
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
            int hargaGalon,
            String namaKurir,
            String metodePengiriman,
            int hargaKirim,
            String alamatPengantaran,
            int totalTransaksi,
            String statusTransaksi
    ){
        this.namaPembeli = namaPembeli;
        this.jumlahGalon = jumlahGalon;
        this.hargaGalon = hargaGalon;
        this.hargaKirim = hargaKirim;
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
            int hargaGalon,
            String namaKurir,
            String metodePengiriman,
            int hargaKirim,
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
        this.hargaGalon = hargaGalon;
        this.hargaKirim = hargaKirim;
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

    public int getHargaGalon() {return hargaGalon;}

    public int getHargaKirim() {return hargaKirim;}

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
