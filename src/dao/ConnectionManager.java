package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {

    private static Connection CONNECTION_INSTANCE;
    private final static String URL = "jdbc:mysql://localhost:3306/tp_marmiton";

    private ConnectionManager() {
    }

    public static Connection getConnectionInstance() {
        if (CONNECTION_INSTANCE == null) {
            try {
                loadDriver();
                CONNECTION_INSTANCE = DriverManager.getConnection(URL, "root", "root");
                CONNECTION_INSTANCE.setAutoCommit(false);
            } catch (SQLException e) {
                System.err.println("Connexion impossible");
            }
        }
        return CONNECTION_INSTANCE;
    }

    private static void loadDriver() {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        } catch (SQLException e) {
            System.err.println("Chargement du driver impossible");
        }
    }


    public static void closeConnection() {
        try {
            CONNECTION_INSTANCE.close();
        } catch (Exception e) {
            System.err.println("Fermeture de la connexion impossible");
        }
    }
}