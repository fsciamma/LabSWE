package DAO;

import java.sql.*;

public class CustomerDAO {
    private Connection conn = null;

    public CustomerDAO() {
        try {
            this.conn = getConnection();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException { //TODO forse dovremmo fare una classe DAO da cui ereditano tutte questo metodo
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

    public boolean addNewCustomer(String fn, String ln, String em){
        boolean success = false;
        ResultSet rs;
        try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            rs = stmt.executeQuery("select * from customer");

            rs.moveToInsertRow();

            rs.updateString("first_name", fn);
            rs.updateString("last_name", ln);
            rs.updateString("email", em);

            rs.insertRow();
            rs.beforeFirst();

            success = true;
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return success;
    }
}
