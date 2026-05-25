package com.fauzan.mygalon.controller;

import com.fauzan.mygalon.DatabaseConn;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import  javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
    private String username;
    private String password;

    @FXML
    private TextField inputName;

    @FXML
    private PasswordField inputPass;

    @FXML
    private Label loginStatus;

    public void validasiLogin(){
        username = inputName.getText();
        password = inputPass.getText();

//        cek input
        if (username.isEmpty() || password.isEmpty()){
            loginStatus.setText("Error! Kolom tidak boleh kosong!");
            return;
        }

        String sql = """
                SELECT * FROM admin
                WHERE username = ?
                AND password = ?
                """;

//        validasi data
        try (
                Connection connection = DatabaseConn.connect();
                PreparedStatement statement = connection.prepareStatement(sql);
                )
        {

//            prepare statement
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            if(result.next()){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fauzan/mygalon/dashboard.fxml"));
                Parent root = loader.load();

                DashboardController dashboardController =
                        loader.getController();

//                set usernameLabel di dashboard
                dashboardController.setUsernameLabel(username);

//                ambil lokasi window/stage
                Stage stage = (Stage) inputName.getScene().getWindow();

                Scene scene = new Scene(root);

                stage.setScene(scene);
            }

            else {
                loginStatus.setText("Error! Username atau Password salah!");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
