package com.fauzan.mygalon;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConn {

    public static Connection connect(){
        try {
            String url = "jdbc:sqlite:mygalon.db";

            return DriverManager.getConnection(url);

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}