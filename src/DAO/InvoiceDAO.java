package DAO;

import model.Invoice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class InvoiceDAO extends BaseDAO{
    private static InvoiceDAO INSTANCE;
    private InvoiceDAO(){}

    public static InvoiceDAO getINSTANCE() {
        if(INSTANCE == null){
            INSTANCE = new InvoiceDAO();
        }
        return INSTANCE;
    }

    /**
     * Metodo per recuperare una riga dal database con l'abiettivo di modificarla
     * @param query: Query utilizzata per recuperare i dati
     * @return L'Invoice da modificare
     */
    private Invoice getInvoice(String query) throws SQLException { //TODO può ritornare void?
        Invoice i = new Invoice();
        try(Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                i.setInvoiceID(rs.getInt("invoiceid"));
                i.setInvoice_amount(rs.getBigDecimal("invoice_amount"));
                i.setPaid(rs.getBoolean("is_paid"));
            }
        }
        if(i.getInvoiceID() == 0) {
            throw new SQLException("Non è stata trovata la ricevuta cercata");
        }
        return i;
    }

    /**
     * Metodo per mostrare a scehrmo una o più righe del database
     * @param query: Query utilizzata per recuperare i dati
     * @return true se è stata trovata almeno una Invoice che rispetti la query effettuata, false altrimenti
     * @throws SQLException
     */
    private boolean showInvoices(String query) throws SQLException{
        boolean isFound = true;
        ArrayList<Invoice> iList = new ArrayList<>();
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                Invoice i = new Invoice();
                i.setInvoiceID(rs.getInt("invoiceid"));
                i.setInvoice_amount(rs.getBigDecimal("invoice_amount"));
                i.setPaid(rs.getBoolean("is_paid"));
                iList.add(i);
            }
        }
        if(iList.isEmpty()){
            isFound = false;
        }
        for (Invoice i: iList) {
            System.out.println(i);
        }
        return isFound;
    }

    /**
     * Metodo utilizzato per inserire una nuova Invoice nel database
     * @param i: Invoice da aggiungere al database
     */
    public void addNewInvoice(Invoice i){
        String query = "select * from customerinvoice";
        ResultSet rs;
        try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            rs = stmt.executeQuery(query);

            rs.moveToInsertRow();

            rs.updateInt("invoiceid", i.getInvoiceID());
            rs.updateBigDecimal("invoice_amount", i.getInvoice_amount()/*new BigDecimal(Float.toString(i.getInvoice_amount()))*/); //TODO capire se conviene più usare BigDecimal o float
            rs.updateBoolean("is_paid", i.isPaid());

            rs.insertRow();
            rs.beforeFirst();

        } catch(SQLException e){
            System.err.println(e.getMessage());
            throw new RuntimeException("Non è stata aggiunta la nuova ricevuta al database...");
        }
    }

    /**
     * Metodo che mostra a schermo l'Invoice che ha l'ID richiesto
     * @param id: ID dell'Invoice da cercare
     * @return L'oggetto Invoice cercato
     */
    public Invoice findByInvoiceID(int id) throws SQLException{
        String query = "select * from customerinvoice where invoiceid = " + id;
        return getInvoice(query);
    }

    /**
     * Metodo che mostra a schermo le Invoice relative al Customer con l'ID richiesto
     * @param id: ID del Customer relativo alle Invoice che voglio cercare
     */
    public void findByCustomerID(int id) throws SQLException{
        String query = "select * from customerinvoice where customerid = " + id;
        if(!showInvoices(query)){
            throw new SQLException("La ricevuta del cliente " + id + " non è stata trovata");
        }
    }

    /**
     * Metodo che mostra a schermo tutte le Invoice o pagate o ancora da pagare
     * @param status: Variabile booleana che indica se cercare le Invoice pagate o quelle non pagate
     */
    public void findByPaymentStatus(boolean status) throws SQLException {
        String query = "select * from customerinvoice where is_paid = " + status;
        if(!showInvoices(query)){
            throw new SQLException("Non sono state trovate ricevute con questo stato");
        }
    }

    /**
     * Metodo che permette di eliminare una riga dalla tabella customerinvoice
     * @param invCode: identificativo della ricevuta da cancellare
     */
    public void deleteInvoice(int invCode) {
        String query = "delete from customerinvoice where invoiceid = " + invCode;
        try(Statement stmt = conn.createStatement()){
            stmt.execute(query);
            System.out.println("La ricevuta è stata cancellata!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
