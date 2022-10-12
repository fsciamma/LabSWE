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
     * Permette di aggiungere un nuovo cliente al database
     */
    public void addNewCustomer(){ //TODO decidere se questo metodo sta in questa classe o va inserita una classe SystemDAO che crea nuovi oggetti da inserire nel db
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

        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public Customer findById(int id) throws SQLException {
        Customer c = new Customer();
        String query = "select * from customer where customerid = " + id;
        try {
            return getCustomer(query, c);
        } catch (SQLException e){
            System.out.println("Il cliente #" +  id + " non è presente nel database");
        }
        return c;
    }

    public Customer findByInfo(String fn, String em) throws SQLException {
        String[] fullName = fn.split(" "); //dà per assunto che n sia composto di un nome e un cognome
        String query = "select * from customer where first_name = '" + fullName[0] + "' and last_name = '" + fullName[1] + "' and email = '" + em + "'";
        Customer c = new Customer();
        try {
            return getCustomer(query, c);
        } catch (SQLException e){
            System.out.println("Cliente non trovato: i dati inseriti non risultano nel database");
        }
        return c;
    }

    private Customer getCustomer(String query, Customer c) throws SQLException {
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                c.set_customerID(rs.getInt("customerid"));
                c.set_first_name(rs.getString("first_name"));
                c.set_last_name(rs.getString("last_name"));
                c.set_email(rs.getString("email"));
            }
        }
        if(c.get_customerID() == 0){
            throw new SQLException();
        }
        System.out.println(c);
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
        if(cList.isEmpty()){
            throw new SQLException();
        }
        return cList;
    }

    /**
     *
     * @param
     */
    public void updateCustomerInfo(int id){
        try {
            this.updateInfo(findById(id), id);
        } catch (SQLException e){
            System.out.println("Il cliente #" + id + " non è registrato.");
        }
    }

    public void updateCustomerInfo(String n){
        try {
            String[] fullName = n.split(" "); //dà per assunto che n sia composto di un nome e un cognome
            Customer cList = findByInfo(fullName[0], fullName[1]);
            //updateInfo(cList.get(0), cList.get(0).get_customerID());
        } catch (SQLException e){
            System.out.println("Non è stato trovato nessun cliente " + n);
        }
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
