package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseDAO {

    static Connection conn = null; //Fare un singleton per avere una connessione comune a tutti gli ObjectDAO: viene condivisa la connessione fra tutti i DAO

    /**
     * Costruttore della superclass BaseDAO: viene chiamato da tutti i DAO che vengono istanziati, che condividono tutti lo stesso attributo Connection: in questo modo, solo la prima volta che viene istanziato un DAO viene creata la connessione, gli altri useranno quella in comune.
     */
    public BaseDAO() {
        try {
            conn = getConnection();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Metodo che istanzia la connessione col database utilizzato
     * @return Oggetto Connection condiviso tra tutti i DAO
     * @throws SQLException
     */
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
            System.err.println(var5.getMessage());
            return null;
        }
    }
}
