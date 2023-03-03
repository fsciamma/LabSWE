package DAO;

import model.Customer;

import java.sql.*;
import java.util.ArrayList;

public class CustomerDAO extends BaseDAO {

    private static CustomerDAO INSTANCE;

    private CustomerDAO() {
        super();
    }

    public static CustomerDAO getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new CustomerDAO();
        }
        return INSTANCE;
    }


    /**
     * Permette di aggiungere un nuovo cliente al database
     * @param newC Il Customer da inserire nel database
     * @throws SQLException lancia un'eccezione se nel database esiste già un cliente registrato con la stessa e-mail
     */
    public void addNewCustomer(Customer newC) throws SQLException{
        String query = "select * from \"laZattera\".customer";
        ResultSet rs;
        try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            rs = stmt.executeQuery(query);

            rs.moveToInsertRow();

            rs.updateString("name", newC.get_first_name());
            rs.updateString("surname", newC.get_last_name());
            rs.updateString("email", newC.get_email());

            rs.insertRow();
            rs.beforeFirst();

        }
    }

    /**
     * Metodo che restituisce un cliente (chiave primaria) data la sua email
     *
     * @param em: mail del cliente da cercare
     * @return Customer: istanza del cliente cercato con le informazioni trovate nel DB usando la chiave primaria
     */
    public Customer findByEMail(String em) throws SQLException {
        String query = "select * from \"laZattera\".customer where email = '" + em + "'";
        return getCustomer(query);
    }

    /**
     * Cerca le informazioni su un Customer nel database
     *
     * @param query La query da usare per cercare il Customer nel database
     * @return Customer c: contenente le informazioni estratte dal database
     */
    private Customer getCustomer(String query) throws SQLException {
        Customer c = new Customer();
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                c.set_first_name(rs.getString("name"));
                c.set_last_name(rs.getString("surname"));
                c.set_email(rs.getString("email"));
            }
        }
        if(c.get_email() == null){
            throw new SQLException("Il cliente non è stato trovato...");
        }
        return c;
    }

    /**
     * Printa a schermo la prima riga di una tabella dove verranno poi mostrati tutti i clienti. Chiama showCustomers() che scrive poi i clienti completi d'informazioni
     */
    public void findAll(){
        String query = "select * from \"laZattera\".customer";
        System.out.println(String.format("%-15s", "Nome") + "|" + String.format("%-15s", "Cognome") + "|" + String.format("%-35s", "E-mail"));
        System.out.println(String.format("%40s", "").replace("", "_"));
        try{
            showCustomers(query);
        } catch (SQLException e) {
            System.err.println(e.getMessage()+".");
        }
    }

    /**
     * Metodo che printa a schermo in una tabella tutti i clienti che matchano il nome inserito
     * @param ln: nome dei clienti da cercare
     */
    public void findByFirstName(String ln){
        String query = "select * from \"laZattera\".customer where name = '" + ln +"'";
        tabulateFindBy(query);
    }

    /**
     * Metodo che printa a schermo in una tabella tutti i clienti che matchano il cognome inserito
     * @param ln: cognome dei clienti da cercare
     */
    public void findByLastName(String ln){
        String query = "select * from \"laZattera\".customer where surname = '" + ln +"'";
        tabulateFindBy(query);
    }


    /**
     * Metodo che printa a schermo in una tabella tutti i clienti che matchano nome e cognome inseriti
     * @param full_name: nome e cognome dei clienti da cercare
     */
    public void findByFullName(String[] full_name){
        String query = "select * from \"laZattera\".customer where name = '" + full_name[0] + "' and surname = '" + full_name[1] + "'";
        tabulateFindBy(query);
    }

    /**
     * Printa a schermo una tabella con le informazioni dei clienti ottenuti dai metodi di ricerca specifici(findByName(), findBySurname()...)
     * @param query La query generata dal metodo di ricerca che deve essere eseguita sul database
     */
    private void tabulateFindBy(String query) {
        System.out.println("Cod. " + "|" + String.format("%-15s", "Nome") + "|" + String.format("%-15s", "Cognome") + "|" + String.format("%-35s", "E-mail"));
        System.out.println(String.format("%40s", "").replace("", "_"));
        try{
            showCustomers(query);
        } catch(SQLException e){
            System.err.println(e.getMessage() + " con i dati forniti.");
        }
    }

    /**
     * Printa a schermo le informazioni sui clienti ritornati dalla query
     * @param query La query generata dal metodo di ricerca che deve essere eseguita sul database
     */
    private void showCustomers(String query) throws SQLException {
        ArrayList<Customer> cList = new ArrayList<>();
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                Customer c = new Customer();
                c.set_first_name(rs.getString("name"));
                c.set_last_name(rs.getString("surname"));
                c.set_email(rs.getString("email"));
                cList.add(c);
            }
        }
        if(cList.isEmpty()){
            throw new SQLException("Non sono stati trovati clienti");
        }
        for (Customer c: cList) {
            System.out.println(c.tabulated());
        }
    }

    /**
     * Cerca se nel database è già presente un cliente con quei dati: se sì, ritorna un errore e non esegue la modifica sul database
     * @param c oggetto Customer contenente le informazioni da modificare
     * @return true se le credenziali sul db sono state modificate con successo, false altrimenti
     */
    public boolean updateInfo(Customer c){
        try {
            //findHomonym(c);
            String query = "select * from \"laZattera\".customer where email = '" + c.get_email() + "'";
            try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){
                ResultSet rs = stmt.executeQuery(query);
                while(rs.next()) {
                    rs.updateString("name", c.get_first_name());
                    rs.updateString("surname", c.get_last_name());
                    rs.updateString("email", c.get_email());
                    rs.updateRow();
                }
            } catch (SQLException e){
                System.err.println(e.getMessage());
                return false;
            }
        } catch (RuntimeException e) {
            System.err.println(e.getMessage() + "\nNon è stato possibile modificare le credenziali.");
            return false;
        }
        return true;
    }
}
