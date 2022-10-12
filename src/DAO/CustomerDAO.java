package DAO;

import model.Customer;

import java.sql.*;
import java.util.ArrayList;

public class CustomerDAO {
    //TODO probabilmente deve essere un Singleton, e così tutti gli altri ObjectDAO
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

    /**
     * Permette di aggiungere un nuovo cliente al database*
     *
     * @return se l'inserimento è riuscito o meno
     */
    public boolean addNewCustomer(){ //TODO decidere se questo metodo sta in questa classe o va inserita una classe SystemDAO che crea nuovi oggetti da inserire nel db
        boolean success = false;
        boolean mailIsValid = false;
        Customer c = new Customer();
        c.set_first_name();
        c.set_last_name();
        while(!mailIsValid) {
            try {
                c.set_email();
                mailIsValid = true;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        String query = "select * from customer";
        ResultSet rs;
        try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            rs = stmt.executeQuery(query);

            rs.moveToInsertRow();

            rs.updateString("first_name", c.get_first_name());
            rs.updateString("last_name", c.get_last_name());
            rs.updateString("email", c.get_email());

            rs.insertRow();
            rs.beforeFirst();

            success = true;
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return success;
    }

    public Customer findById(int id) throws SQLException {
        Customer c = new Customer();
        String query = "select * from customer where customerid = " + id;
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                c.set_customerID(rs.getInt("customerid"));
                c.set_first_name(rs.getString("first_name"));
                c.set_last_name(rs.getString("last_name"));
                c.set_email(rs.getString("email"));
            }
        }
        return c;
    }

    public ArrayList<Customer> findByFirstName(String ln) throws SQLException {
        ArrayList<Customer> cList = new ArrayList<>();
        String query = "select * from customer where last_name = '" + ln +"'";
        return getCustomers(cList, query);
    }

    public ArrayList<Customer> findByLastName(String ln) throws SQLException {
        ArrayList<Customer> cList = new ArrayList<>();
        String query = "select * from customer where last_name = '" + ln +"'";
        return getCustomers(cList, query);
    }

    public ArrayList<Customer> findByFullName(String fn, String ln) throws SQLException {
        ArrayList<Customer> cList = new ArrayList<>();
        String query = "select * from customer where first_name = '" + fn + "' and last_name = '" + ln + "'";
        return getCustomers(cList, query);
    }

    public ArrayList<Customer> findByEMail(String em) throws SQLException {
        ArrayList<Customer> cList = new ArrayList<>();
        String query = "select * from customer where email = '" + em + "'";
        return getCustomers(cList, query);
    }

    private ArrayList<Customer> getCustomers(ArrayList<Customer> cList, String query) throws SQLException {
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                Customer c = new Customer();
                c.set_customerID(rs.getInt("customerid"));
                c.set_first_name(rs.getString("first_name"));
                c.set_last_name(rs.getString("last_name"));
                c.set_email(rs.getString("email"));
                cList.add(c);
            }
        }
        return cList;
    }

    /**
     *
     * @param
     */
    public void updateCustomerInfo(int id) throws SQLException {
        this.updateInfo(findById(id), id);
    }

    public void updateCustomerInfo(String n) throws SQLException { //TODO dovrebbe prendere anche l'indirizzo e-mail
        String[] fullName = n.split(" ");
        ArrayList<Customer> cList = findByFullName(fullName[0], fullName[1]); //TODO viviamo in un mondo ideale in cui non ci sono omonimi :)
        updateInfo(cList.get(0), cList.get(0).get_customerID());
    }

    private void updateInfo(Customer c, int id){
        c.set_first_name();
        c.set_last_name();
        boolean mailIsValid = false;
        while(!mailIsValid) {
            try {
                c.set_email();
                mailIsValid = true;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        String query = "select * from customer where customerid = " + id;
        try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                rs.updateString("first_name", c.get_first_name());
                rs.updateString("last_name", c.get_last_name());
                rs.updateString("email", c.get_email());
                rs.updateRow();
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
