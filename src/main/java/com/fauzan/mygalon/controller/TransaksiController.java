package com.fauzan.mygalon.controller;

import com.fauzan.mygalon.DatabaseConn;
import com.fauzan.mygalon.model.Harga;
import com.fauzan.mygalon.model.Transaksi;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class TransaksiController {

    private int hargaGalon;
    private int hargaKirim;

    //ambil harga bawaan tabel untuk update
    private  int baseHargaGalon;
    private  int baseHargaKirim;

    //harga
    Harga harga;

    private String username;
    private int totalTransaksi = 0;

//    selected row tabel
    private int selectedId = -1;

//    update mode
    private boolean modeEdit = false;

    @FXML
    private TextField inputNama;

    @FXML
    private TextField inputJumlahGalon ;

    @FXML
    private TextField inputKurir ;

    @FXML
    private ComboBox<String> inputMetode;

    @FXML
    private TextArea inputAlamat;

    @FXML
    private ComboBox<String> inputStatus;

    @FXML
    private Label labelStatus;

    @FXML
    private Label totalLabel;

    // tabel
    @FXML
    private TableView<Transaksi> tableTransaksi;

    @FXML
    private TableColumn<Transaksi, Integer> colNum;

    @FXML
    private TableColumn<Transaksi, String> colNama;

    @FXML
    private TableColumn<Transaksi, Integer> colTotal;

    @FXML
    private TableColumn<Transaksi, Integer> colJumlah;

    @FXML
    private TableColumn<Transaksi, String> colKurir;

    @FXML
    private TableColumn<Transaksi, String> colAlamat;

    @FXML
    private TableColumn<Transaksi, String> colStatus;

    @FXML
    private TableColumn<Transaksi, String> colTanggal;

    @FXML
    private TableColumn<Transaksi, String> colMetode;

    @FXML
    private TableColumn<Transaksi, Integer> colHargaGalon;

    @FXML
    private TableColumn<Transaksi, Integer> colHargaKirim;

//    tombol update
    @FXML
    private Button kirimButton;

//    tombol hapus
    @FXML
    private Button hapusButton;

//    sub tombol
    @FXML
    private Button subButton;

    @FXML
    private TextField inputSearch;

    @FXML
    private ComboBox<String> filterSearch;

    ObservableList<Transaksi> list;

    FilteredList<Transaksi> filterList;

    public void loadTable(){

//        atur ukuran colom
        tableTransaksi.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

//        set nomor otomatis
        colNum.setCellValueFactory(cellData ->

                new ReadOnlyObjectWrapper<>(
                        tableTransaksi.getItems().indexOf(
                                cellData.getValue()
                        ) + 1
                )
        );

//        connect kolom dengan getter
        colNama.setCellValueFactory(
                new PropertyValueFactory<>("namaPembeli")
        );

        colTotal.setCellValueFactory(
                new PropertyValueFactory<>("totalTransaksi")
        );

        colJumlah.setCellValueFactory(
                new PropertyValueFactory<>("jumlahGalon")
        );

        colKurir.setCellValueFactory(
                new PropertyValueFactory<>("namaKurir")
        );

        colAlamat.setCellValueFactory(
                new PropertyValueFactory<>("alamatPengantaran")
        );

        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("statusTransaksi")
        );

        colTanggal.setCellValueFactory(
                new PropertyValueFactory<>("tanggalTransaksi")
        );

        colMetode.setCellValueFactory(
                new PropertyValueFactory<>("metodePengiriman")
        );

        colHargaGalon.setCellValueFactory(
                new PropertyValueFactory<>("hargaGalon")
        );

        colHargaKirim.setCellValueFactory(
                new PropertyValueFactory<>("hargaKirim")
        );

        try (
                Connection connection = DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery("SELECT * FROM transaksi");
        ) {
//            bersihkan tabel
            list.clear();

            while(data.next()){
                Transaksi transaksi = new Transaksi(
                        data.getInt("idTransaksi"),
                        data.getString("namaPembeli"),
                        data.getInt("jumlahGalon"),
                        data.getInt("hargaGalon"),
                        data.getString("namaKurir"),
                        data.getString("metodePengiriman"),
                        data.getInt("hargaKirim"),
                        data.getString("alamatPengantaran"),
                        data.getString("tanggalTransaksi"),
                        data.getInt("totalTransaksi"),
                        data.getString("statusTransaksi")
                );
//                masukan ke list
                list.add(transaksi);
            }
            applyFilter();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void getHarga(){
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

    //set username
    public void setUsername(String nama){
        this.username = nama;
    }

    public void bukaDashboard(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fauzan/mygalon/dashboard.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) inputNama.getScene().getWindow();

            DashboardController controller =
                    loader.getController();

            controller.setUsername(username);

            stage.getScene().setRoot(root);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void bukaHarga(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fauzan/mygalon/harga.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) inputNama.getScene().getWindow();

            HargaController controller =
                    loader.getController();

            controller.setUsername(username);

            stage.getScene().setRoot(root);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateTotalLabel(){
        if(inputJumlahGalon.getText().isEmpty() || !inputJumlahGalon.getText().matches("\\d+")) {
            totalLabel.setText("Rp 0");
            return;
        }

        int jumlahGalon = Integer.parseInt(inputJumlahGalon.getText());

        int currentHargaGalon;
        int currentHargaKirim;

        if(modeEdit){
            currentHargaGalon = baseHargaGalon;
            currentHargaKirim = baseHargaKirim;
        }
        else {
            currentHargaGalon = hargaGalon;
            currentHargaKirim = hargaKirim;
        }

        if(inputMetode.getValue().equals("diantar")){
            totalTransaksi = jumlahGalon * (currentHargaGalon + currentHargaKirim);
        }
        else {
            totalTransaksi = jumlahGalon * currentHargaGalon;
        }

        totalLabel.setText("Rp " + totalTransaksi);
    }

    public void initialize(){
        //initialize
        list = FXCollections.observableArrayList();
        filterList = new FilteredList<>(list, b -> true);


        //ambil harga
        getHarga();
        hargaGalon = harga.getHargaGalon();
        hargaKirim = harga.getHargaKirim();

        inputJumlahGalon.textProperty().addListener(
                ((observable, oldValue, newValue) ->
                        updateTotalLabel())
        );

        inputMetode.valueProperty().addListener(
                ((observable, oldValue, newValue) ->
                        updateTotalLabel())
        );

        inputStatus.getItems().addAll(
                "diproses",
                "selesai"
        );

        inputMetode.getItems().addAll(
                "ambil",
                "diantar"
        );

        filterSearch.getItems().addAll(
                "Nama A-Z",
                "Nama Z-A",
                "Terbaru",
                "Terlama",
                "Status"
        );

        // default value
        inputMetode.setValue("ambil");
        inputStatus.setValue("diproses");

        //load table
        loadTable();
        eventSearch();
        eventFilter();

        //default filter
        filterSearch.setValue("Terbaru");

        getTableRow();
        setKirimButton();

    }

    public void applyFilter(){

        String newValue = filterSearch.getValue();

        if(newValue == null){
            return;
        }

        switch (newValue){

            case "Nama A-Z" ->
                    FXCollections.sort(
                            list,
                            (a,b) ->
                                    a.getNamaPembeli()
                                            .compareToIgnoreCase(
                                                    b.getNamaPembeli()
                                            )
                    );

            case "Nama Z-A" ->
                    FXCollections.sort(
                            list,
                            (a,b) ->
                                    b.getNamaPembeli()
                                            .compareToIgnoreCase(
                                                    a.getNamaPembeli()
                                            )
                    );

            case "Terbaru" ->
                    FXCollections.sort(
                            list,
                            (a,b) ->
                                    b.getTanggalTransaksi()
                                            .compareTo(
                                                    a.getTanggalTransaksi()
                                            )
                    );

            case "Status" ->
                    FXCollections.sort(
                            list,
                            (a,b) ->
                                    a.getStatusTransaksi()
                                            .compareTo(
                                                    b.getStatusTransaksi()
                                            )
                    );

            case "Terlama" ->
                    FXCollections.sort(
                            list,
                            (a,b) ->
                                    a.getTanggalTransaksi()
                                            .compareTo(
                                                    b.getTanggalTransaksi()
                                            )
                    );
        }
    }

    public void eventFilter(){
        filterSearch.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
            applyFilter();
                });
    }

    public void setKirimButton(){
        if (modeEdit){
            kirimButton.setText("Perbarui");
            subButton.setText("Cancel");
            hapusButton.setManaged(true);
            hapusButton.setVisible(true);
        }
        else {
            kirimButton.setText("Buat");
            subButton.setText("Reset");
            hapusButton.setManaged(false);
            hapusButton.setVisible(false);
        }
    }

    public void eventSearch(){
        inputSearch.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    filterList.setPredicate(transaksi -> {
                        if(newValue == null || newValue.isEmpty()){
                            return true;
                        }

                        //ubah jadi huruf kecil
                        String huruf = newValue.toLowerCase();

                        //validasi null
                        String nama = transaksi.getNamaPembeli();

                        return nama != null && nama.toLowerCase()
                                .contains(huruf);
                    });
                });
        tableTransaksi.setItems(filterList);
    }

    //select tabel row
    public void getTableRow(){
        tableTransaksi.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedData) -> {

                    if (selectedData != null) {
                        selectedId = selectedData.getIdTransaksi();

                        modeEdit = true;

//                        ambil harga sesuai tabel
                        baseHargaGalon = selectedData.getHargaGalon();
                        baseHargaKirim = selectedData.getHargaKirim();

                        inputNama.setText(
                                selectedData.getNamaPembeli()
                        );

                        inputMetode.setValue(
                                selectedData.getMetodePengiriman()
                        );

                        inputJumlahGalon.setText(
                                String.valueOf(selectedData.getJumlahGalon())
                        );
                        inputKurir.setText(
                                selectedData.getNamaKurir()
                        );
                        inputAlamat.setText(
                                selectedData.getAlamatPengantaran()
                        );

                        inputStatus.setValue(
                                selectedData.getStatusTransaksi()
                        );

                        // paksa hitung ulang
                        updateTotalLabel();

                        setKirimButton();
                    }
                });
    }

    public boolean validasi(){
        if(inputJumlahGalon.getText().isEmpty()){
            labelStatus.setVisible(true);
            labelStatus.setManaged(true);
            labelStatus.setText("Error! Kolom Jumlah galon harus diisi!");
            labelStatus.getStyleClass().setAll("status-error");
            return false;
        }

//        validasi inputJumlahGalon
        if(!inputJumlahGalon.getText().matches("\\d+")) {
            labelStatus.setVisible(true);
            labelStatus.setManaged(true);
            labelStatus.setText("Error! Jumlah galon harus angka");
            labelStatus.getStyleClass().setAll("status-error");
            return false;
        }

        return true;
    }

    public void tambahTransaksi(){

//        validasi
        if(!validasi()){
            return;
        }

//        ambil nilai
        String status = inputStatus.getValue();
        String metode = inputMetode.getValue();

//        konversi
        int jumlahGalon = Integer.parseInt(inputJumlahGalon.getText());

//        hitung harga
        if(metode.equals("diantar")){
            totalTransaksi = jumlahGalon * (hargaGalon + hargaKirim);
        }
        else {
            totalTransaksi = jumlahGalon * hargaGalon;
        }

        Transaksi transaksi = new Transaksi(
                inputNama.getText(),
                jumlahGalon,
                hargaGalon,
                inputKurir.getText(),
                metode,
                hargaKirim,
                inputAlamat.getText(),
                totalTransaksi,
                status
        );

        //        query
        String sql = """
                INSERT INTO transaksi (
                    namaPembeli,
                    jumlahGalon,
                    hargaGalon,
                    namaKurir,
                    metodePengiriman,
                    hargaKirim,
                    alamatPengantaran,
                    totalTransaksi,
                    statusTransaksi
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try(
                Connection connection = DatabaseConn.connect();
                PreparedStatement statement = connection.prepareStatement(sql);
                )
        {

            statement.setString(1,
                    transaksi.getNamaPembeli());

            statement.setInt(2,
                    transaksi.getJumlahGalon());

            statement.setInt(3,
                    transaksi.getHargaGalon());

            statement.setString(4,
                    transaksi.getNamaKurir());

            statement.setString(5,
                    transaksi.getMetodePengiriman());

            statement.setInt(6,
                    transaksi.getHargaKirim());

            statement.setString(7,
                    transaksi.getAlamatPengantaran());

            statement.setInt(8,
                    transaksi.getTotalTransaksi());

            statement.setString(9,
                    transaksi.getStatusTransaksi());

            statement.executeUpdate();

//            update tabel
            loadTable();


//            status
            labelStatus.getStyleClass().setAll("status-success");
            labelStatus.setManaged(true);
            labelStatus.setVisible(true);
            labelStatus.setText("Succes! Transaksi berhasil di tambahkan!");

            cancelButton();

        } catch (SQLException e) {

            e.printStackTrace();

        }

    }

    public void updateTransaksi(){

        if(selectedId == -1){
            labelStatus.getStyleClass().setAll("status-error");
            labelStatus.setManaged(true);
            labelStatus.setVisible(true);
            labelStatus.setText("Error! Silakan pilih data dulu!");
            return;
        }

        //validasi
        if(!validasi()){
            return;
        }

        String sql = """
            UPDATE transaksi
            SET
                namaPembeli = ?,
                jumlahGalon = ?,
                hargaGalon = ?,
                namaKurir = ?,
                metodePengiriman = ?,
                hargaKirim = ?,
                alamatPengantaran = ?,
                totalTransaksi = ?,
                statusTransaksi = ?
            WHERE idTransaksi = ?
            """;

        try(
                Connection connection = DatabaseConn.connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
        ){

            String metode = inputMetode.getValue();

            int jumlahGalon =
                    Integer.parseInt(
                            inputJumlahGalon.getText()
                    );

            if(metode.equals("diantar")){
                totalTransaksi =
                        jumlahGalon *
                                (baseHargaGalon + baseHargaKirim);
            }
            else{
                totalTransaksi =
                        jumlahGalon * baseHargaGalon;
            }

            statement.setString(1,
                    inputNama.getText());

            statement.setInt(2,
                    jumlahGalon);

            statement.setInt(3,
                    baseHargaGalon);

            statement.setString(4,
                    inputKurir.getText());

            statement.setString(5,
                    metode);

            statement.setInt(6,
                    baseHargaKirim);


            statement.setString(7,
                    inputAlamat.getText());

            statement.setInt(8,
                    totalTransaksi);

            statement.setString(9,
                    inputStatus.getValue());

            statement.setInt(10,
                    selectedId);

            statement.executeUpdate();

            //update tabel
            loadTable();

            labelStatus.getStyleClass().setAll("status-success");
            labelStatus.setManaged(true);
            labelStatus.setVisible(true);
            labelStatus.setText("Succes! Transaksi berhasil di perbarui!");

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteTransaksi(){
//        alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Konfirmasi Delete");
        alert.setHeaderText("Yakin ingin menghapus Transaksi ini?");
        alert.setContentText(
                "Data yang dihapus tidak bisa dikembalikan."
        );

        DialogPane dialogPane = alert.getDialogPane();

        dialogPane.getStylesheets().add(
                getClass()
                        .getResource("/com/fauzan/mygalon/style.css")
                        .toExternalForm()
        );

        dialogPane.getStyleClass().add("custom-alert");

        Optional<ButtonType> hasil = alert.showAndWait();

        String sql = "DELETE FROM transaksi WHERE idTransaksi = ?";


        if (hasil.isPresent() && hasil.get() == ButtonType.OK){
            try (
                    Connection connection = DatabaseConn.connect();
                    PreparedStatement stmt = connection.prepareStatement(sql);
            ){
                stmt.setInt(1, selectedId);
                stmt.executeUpdate();
                loadTable();
                cancelButton();

                labelStatus.getStyleClass().setAll("status-success");
                labelStatus.setManaged(true);
                labelStatus.setVisible(true);
                labelStatus.setText("Succes! Transaksi berhasil di hapus!");

            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void kirimTransaksi(){
        if(modeEdit){
            updateTransaksi();
        }
        else {
            tambahTransaksi();
        }
    }

    public void cancelButton(){
        inputNama.clear();

        inputJumlahGalon.clear();

        inputKurir.clear();

        inputAlamat.clear();

        inputMetode.setValue("ambil");

        inputStatus.setValue("diproses");

        selectedId = -1;

        baseHargaGalon = 0;

        baseHargaKirim = 0;

        tableTransaksi.getSelectionModel().clearSelection();

        modeEdit = false;
        setKirimButton();
    }

}