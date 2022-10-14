package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseDAO {

    static Connection conn = null; //Fare un singleton per avere una connessione comune a tutti gli ObjectDAO: viene condivisa la connessione fra tutti i DAO

    public BaseDAO() {
        try {
            if(conn == null) {
                conn = getConnection();
            }
            else{
                System.out.println("Esiste già una connessione: " + conn.toString()); //TODO riga da cancellare alla consegna del progetto
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found " + e);
        }
        try {
            String host = "jdbc:postgresql://localhost:5432/laZattera_db";
            String uName = "filippos";
            String uPass = "filippos";
            return DriverManager.getConnection(host, uName, uPass);
        } catch (SQLException var5) {
            System.out.println(var5.getMessage());
            return null;
        }
    }
}
