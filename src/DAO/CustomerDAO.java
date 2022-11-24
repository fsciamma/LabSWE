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
     */
    public void addNewCustomer(Customer newC){
        try {
            findHomonym(newC);
            //prosegue se non ha trovato un customer con gli stessi dati
            String query = "select * from customer";
            ResultSet rs;
            try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){
                rs = stmt.executeQuery(query);

                rs.moveToInsertRow();

                rs.updateString("name", newC.get_first_name());
                rs.updateString("surname", newC.get_last_name());
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
     * Metodo per cercare nel database clienti con la stessa tripla univoca del cliente che si prova a inserire; se trova un cliente già registrato con le stesse credenziali lancia un'eccezione
     * @param newC oggetto Customer di cui si cerca un omonimo
     */
    //TODO da rivedere, potrebbe non servire più o comunque potrebbe ridursi a un check sulla email
    private void findHomonym(Customer newC) throws RuntimeException {
        String query = "select * from customer where name = '" + newC.get_first_name() + "' and surname = '" + newC.get_last_name() + "' and email = '" + newC.get_email() + "'";
        try {
            getCustomer(query);
            throw new RuntimeException("Cliente con stesse credenziali già registrato..."); //Viene lanciata solo se getCustomer non lancia la sua
        } catch (SQLException ignored) {
            //Non è stato trovato nessun cliente con le stesse credenziali, quindi si può inserire senza problemi il nuovo cliente
        }
    }

    /**
     * Metodo che restituisce un cliente (chiave primaria) data la sua email
     *
     * @param em: mail del cliente da cercare
     * @return Customer: istanza del cliente cercato con le informazioni trovate nel DB usando la chiave primaria
     */
    public Customer findByEMail(String em) throws SQLException {
        String query = "select * from customer where email = '" + em + "'";
        return getCustomer(query);
    }

    /**
     * Permette la ricerca di un cliente nel database usando la tripla chiave (Nome, Cognome, E-mail)
     *
     * @param fn Prende il nome completo del Customer, che deve essere inserito separando Nome e Cognome con uno spazio
     * @param em Prende l'indirizzo e-mail del Customer
     * @return Customer c: oggetto Customer che presenta le informazioni ottenute dal DB usando la chiave (Nome, Cognome, E-mail)
     */
    public Customer findByInfo(String fn, String em) throws SQLException{
        String[] fullName = fn.split(" "); //dà per assunto che n sia composto di un nome e un cognome
        String query = "select * from customer where name = '" + fullName[0] + "' and surname = '" + fullName[1] + "' and email = '" + em + "'";
        return getCustomer(query);
    }

    /**
     * Cerca le informazioni su un Customer nel database
     *
     * @param query La query da usare per cercare il Customer nel database
     * @return Customer c: contenente le informazioni estratte dal database
     * @throws SQLException
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
        String query = "select * from customer";
        System.out.println("Cod. " + "|" + String.format("%-15s", "Nome") + "|" + String.format("%-15s", "Cognome") + "|" + String.format("%-35s", "E-mail"));
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
    public void findByFirstName(String ln){ //TODO possibile renderlo case insensitive
        String query = "select * from customer where name = '" + ln +"'";
        tabulateFindBy(query);
    }

    /**
     * Metodo che printa a schermo in una tabella tutti i clienti che matchano il cognome inserito
     * @param ln: cognome dei clienti da cercare
     */
    public void findByLastName(String ln){
        String query = "select * from customer where surname = '" + ln +"'";
        tabulateFindBy(query);
    }


    /**
     * Metodo che printa a schermo in una tabella tutti i clienti che matchano nome e cognome inseriti
     * @param full_name: nome e cognome dei clienti da cercare
     */
    public void findByFullName(String[] full_name){
        String query = "select * from customer where name = '" + full_name[0] + "' and surname = '" + full_name[1] + "'";
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
            //TODO valutare se aggiungere una wait per far printare l'errore subito sotto alla tabella
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
    public boolean updateInfo(Customer c){ //TODO valutare se può essere un metodo comune a tutti gli ObjectDAO, nel caso, ognuno esegue un proprio override
        try {
            findHomonym(c);
            String query = "select * from customer where email = " + c.get_email();
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
                //TODO deve ritornare false? Probabilmente sì, perché non è riuscito ad accedere al db...
            }
        } catch (RuntimeException e) {
            System.err.println(e.getMessage() + "\nNon è stato possibile modificare le credenziali.");
            return false;
        }
        return true;
    }
}
