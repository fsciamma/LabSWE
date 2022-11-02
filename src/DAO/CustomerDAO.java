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
     */
    //TODO rielaborare le funzioni secondo nuovo schema
    public void addNewCustomer(Customer newC){ //TODO decidere se questo metodo sta in questa classe o va inserita una classe SystemDAO che crea nuovi oggetti da inserire nel db
        try {
            findHomonym(newC);
            //prosegue se non ha trovato un customer con gli stessi dati
            String query = "select * from customer";
            ResultSet rs;
            try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){
                rs = stmt.executeQuery(query);

                rs.moveToInsertRow();

                rs.updateString("first_name", newC.get_first_name());
                rs.updateString("last_name", newC.get_last_name());
                rs.updateString("email", newC.get_email());

                rs.insertRow();
                rs.beforeFirst();

            } catch(SQLException e){
                System.err.println(e.getMessage());
            }
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Metodo per cercare nel database clienti con la stessa tripla univoca del cliente che si prova ad inserire; se trova un cliente già registrato con le stesse credenziali lancia un'eccezione
     * @param newC oggetto Customer di cui si cerca un omonimo
     */
    private void findHomonym(Customer newC) throws RuntimeException {
        String query = "select * from customer where first_name = '" + newC.get_first_name() + "' and last_name = '" + newC.get_last_name() + "' and email = '" + newC.get_email() + "'";
        Customer c = new Customer();
        try {
            getCustomer(query, c);
            throw new RuntimeException("Cliente con stesse credenziali già registrato..."); //Viene lanciata solo se getCustomer non lancia la sua
        } catch (SQLException ignored) {
            //Non è stato trovato nessun cliente con le stesse credenziali, quindi si può inserire senza problemi il nuovo cliente
        }
    }

    /**
     * Permette la ricerca di un cliente nel database usando la chiave primaria CodiceCliente
     *
     * @param id Prende la chiave primaria CodiceCliente
     * @return Customer c: oggetto Customer che presenta le informazioni ottenute dal DB usando la chiave CodiceCliente
     */
    public Customer findById(int id) throws SQLException{
        Customer c = new Customer();
        String query = "select * from customer where customerid = " + id;
        return getCustomer(query, c);
    }

    /**
     * Permette la ricerca di un cliente nel database usando la tripla chiave (Nome, Cognome, E-mail)
     *
     * @param fn Prende il nome completo del Customer, che deve essere inserito separando Nome e Cognome con uno spazio
     * @param em Prende l'indirizzo e-mail del Customer
     * @return Customer c: oggetto Customer che presenta le informazioni ottenute dal DB usando la chiave (Nome, Cognome, E-mail)
     */
    public Customer findByInfo(String fn, String em){
        String[] fullName = fn.split(" "); //dà per assunto che n sia composto di un nome e un cognome
        String query = "select * from customer where first_name = '" + fullName[0] + "' and last_name = '" + fullName[1] + "' and email = '" + em + "'";
        Customer c = new Customer();
        try {
            return getCustomer(query, c);
        } catch (SQLException e){
            System.err.println("Cliente non trovato: i dati inseriti non risultano nel database");
        }
        return c;
    }

    /**
     * Cerca le informazioni su un Customer nel database
     *
     * @param query La query da usare per cercare il Customer nel database
     * @param c Un placeholder che viene poi riempito con le informazioni estratte
     * @return Customer c: contenente le informazioni estratte dal database
     * @throws SQLException
     */
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
            throw new SQLException("Il cliente non è stato trovato");
        }
        return c;
    }

    /**
     * Printa a schermo la prima riga di una tabella dove verranno poi mostrati tutti i clienti. Chiama showCustomers() che scrive poi i clienti completi di informazioni
     */
    public void findAll(){
        String query = "select * from customer";
        System.out.println("Cod. " + "|" + String.format("%-15s", "Nome") + "|" + String.format("%-15s", "Cognome") + "|" + String.format("%-35s", "E-mail"));
        System.out.println(String.format("%40s", "").replace("", "_"));
        try{
            showCustomers(query);
        } catch (SQLException e) {
            System.err.println("Non sono stati trovati clienti");
        }
    }

    public void findByFirstName(String ln){ //TODO possibile renderlo case insensitive
        String query = "select * from customer where first_name = '" + ln +"'";
        tabulateFindBy(query);
    }

    public void findByLastName(String ln){
        String query = "select * from customer where last_name = '" + ln +"'";
        tabulateFindBy(query);
    }

    public void findByEMail(String em){
        String query = "select * from customer where email = '" + em + "'";
        tabulateFindBy(query);
    }

    public void findByFullName(String[] full_name){
        String query = "select * from customer where first_name = '" + full_name[0] + "' and last_name = '" + full_name[1] + "'";
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
            System.err.println("\nNon sono stati trovati clienti con i dati forniti");
            //TODO valutare se aggiungere una wait per far printare l'errore subito sotto alla tabella
        }
    }

    private void showCustomers(String query) throws SQLException {
        ArrayList<Customer> cList = new ArrayList<>();
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
        for (Customer c: cList) {
            System.out.println(c.tabulated());
        }
    }

    /**
     * Cerca se nel database è già presente un cliente con quei dati: se sì, ritorna un errore e non esegue la modifica sul database
     *
     * @param c oggetto Customer contenente le informazioni da modificare
     */
    public void updateInfo(Customer c){ //TODO valutare se può essere un metodo comune a tutti gli ObjectDAO, nel caso, ognuno esegue un proprio override
        try {
            findHomonym(c);
            String query = "select * from customer where customerid = " + c.get_customerID();
            try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){
                ResultSet rs = stmt.executeQuery(query);
                while(rs.next()) {
                    rs.updateString("first_name", c.get_first_name());
                    rs.updateString("last_name", c.get_last_name());
                    rs.updateString("email", c.get_email());
                    rs.updateRow();
                }
            } catch (SQLException e){
                System.err.println(e.getMessage());
            }
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }
}
