package com.fauzan.mygalon.controller;

import com.fauzan.mygalon.DatabaseConn;
import com.fauzan.mygalon.model.Harga;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class HargaController {

    private String username;

    private int hargaGalon;
    private int hargakirim;

    private Harga harga;

    @FXML
    private TextField inputHargaGalon;

    @FXML
    private TextField inputHargaKirim;

    @FXML
    private Label hargaGalonLabel;

    @FXML
    private Label hargaKirimLabel;

    @FXML
    private Label statusLabel;

    public void initialize(){
        //set username
        setUsername(username);

        ambilData();

        //set harga label
        setHargaGalonLabel(harga.getHargaGalon());
        setHargaKirimLabel(harga.getHargaKirim());

        //set base input
        setInputHargaGalon(harga.getHargaGalon());
        setInputHargaKirim(harga.getHargaKirim());
    }

    public void setUsername(String username) {this.username = username;}

    public void setStatusLabel(boolean status, String statusText){
        //Tampilkan
        statusLabel.setManaged(true);
        statusLabel.setVisible(true);

        statusLabel.getStyleClass().removeAll("status-success","status-error");

        if(status){
            statusLabel.getStyleClass().addAll("status-success");
            statusLabel.setText("Succes! " + statusText);
        }
        else{
            statusLabel.getStyleClass().addAll("status-error");
            statusLabel.setText("Error! " + statusText);
        }
    }

    private boolean validasi(){
        if (inputHargaGalon.getText().isEmpty() || inputHargaKirim.getText().isEmpty()){
            setStatusLabel(false, "Kolom harga galon dan kirim harus di isi!");
            return false;
        }

        if (!inputHargaGalon.getText().matches("\\d+") || !inputHargaKirim.getText().matches("\\d+")){
            setStatusLabel(false, "harga galon dan kirim harus angka!");
            return false;
        }
        return true;
    }

    public void ambilData(){
        try(
                Connection connection = DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery("SELECT * FROM harga");
                ){
            while (data.next()){
                harga = new Harga(
                        data.getInt("idHarga"),
                        data.getInt("hargaGalon"),
                        data.getInt("hargaKirim")
                );
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setHargaGalonLabel(int harga){
        hargaGalonLabel.setText("Rp " + harga);
    }

    public void setHargaKirimLabel(int harga){
        hargaKirimLabel.setText("Rp " + harga);
    }

    public void setInputHargaGalon(int harga){
        inputHargaGalon.setText("" + harga);
    }

    public void setInputHargaKirim(int harga){
        inputHargaKirim.setText("" + harga);
    }

    public void bukaDashboard(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fauzan/mygalon/dashboard.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) inputHargaKirim.getScene().getWindow();

            DashboardController controller =
                    loader.getController();

            controller.setUsername(username);

            stage.getScene().setRoot(root);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void bukaTransaksi(){

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/fauzan/mygalon/transaksi.fxml"
                    )
            );

            Parent root = loader.load();

            TransaksiController controller =
                    loader.getController();

            controller.setUsername(username);

            Stage stage = (Stage) inputHargaGalon.getScene().getWindow();

            stage.getScene().setRoot(root);


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateTransaksi(){
        //validasi
        if(!validasi()){
            return;
        };

        //convert
        hargaGalon = Integer.parseInt(inputHargaGalon.getText());
        hargakirim = Integer.parseInt(inputHargaKirim.getText());

        String sql = """
            UPDATE harga SET
            hargaGalon = ?,
            hargaKirim = ?
            WHERE idHarga = ?
            """;
        try(
                Connection connection = DatabaseConn.connect();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ){
            stmt.setInt(1, hargaGalon);
            stmt.setInt(2, hargakirim);
            stmt.setInt(3, harga.getIdHarga());
            stmt.executeUpdate();

            setHargaGalonLabel(hargaGalon);
            setHargaKirimLabel(hargakirim);

            setInputHargaGalon(hargaGalon);
            setInputHargaKirim(hargakirim);

            setStatusLabel(true, "Harga Berhasil di perbarui!");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void resetInput(){
        setInputHargaGalon(0);
        setInputHargaKirim(0);
    }
}
