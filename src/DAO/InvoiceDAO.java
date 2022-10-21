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
     * @param i : Oggetto Invoice che istanzia la riga del database scercata
     * @return L'Invoice da modificare
     */
    private Invoice getInvoice(String query, Invoice i) throws SQLException { //TODO può ritornare void?
        try(Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                i.setInvoiceID(rs.getInt("invoiceid"));
                i.setCustomerID(rs.getInt("customerid"));
                i.setInvoice_amount(rs.getBigDecimal("invoice_amount"));
                i.setPaid(rs.getBoolean("is_paid"));
            }
        }
        if(i.getInvoiceID() == 0) {
            throw new SQLException();
        }
        return i;
    }

    /**
     * Metodo per mostrare a scehrmo una o più righe del database
     * @param query: Query utilizzata per recuperare i dati
     */
    private void showInvoices(String query) throws SQLException{
        ArrayList<Invoice> iList = new ArrayList<>();
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                Invoice i = new Invoice();
                i.setInvoiceID(rs.getInt("invoiceid"));
                i.setCustomerID(rs.getInt("customerid"));
                i.setInvoice_amount(rs.getBigDecimal("invoice_amount"));
                i.setPaid(rs.getBoolean("is_paid"));
                iList.add(i);
            }
        }
        if(iList.isEmpty()){
            throw new SQLException();
        }
        for (Invoice i: iList) {
            System.out.println(i);
        }
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

            rs.updateInt("customerid", i.getCustomerID());
            rs.updateBigDecimal("invoice_amount", i.getInvoice_amount()/*new BigDecimal(Float.toString(i.getInvoice_amount()))*/); //TODO capire se conviene più usare BigDecimal o float
            rs.updateBoolean("is_paid", i.isPaid());

            rs.insertRow();
            rs.beforeFirst();

        } catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Metodo che mostra a schermo l'Invoice che ha l'ID richiesto
     * @param id: ID dell'Invoice da cercare
     */
    public void findByInvoiceID(int id) throws SQLException{
        String query = "select * from customerinvoice where invoiceid = " + id;
        try{
            showInvoices(query);
        } catch(SQLException s){
            throw new SQLException("La ricevuta " + id + " non è stata trovata");
        }
    }

    /**
     * Metodo che mostra a schermo le Invoice relative al Customer con l'ID richiesto
     * @param id: ID del Customer relativo alle Invoice che voglio cercare
     */
    public void findByCustomerID(int id) throws SQLException{
        String query = "select * from customerinvoice where customerid = " + id;
        try{
            showInvoices(query);
        } catch(SQLException s){
            throw new SQLException("La ricevuta del cliente " + id + " non è stata trovata");
        }
    }

    /**
     * Metodo che mostra a schermo tutte le Invoice o pagate o ancora da pagare
     * @param status: Variabile booleana che indica se cercare le Invoice pagate o quelle non pagate
     */
    public void findByPaymentStatus(boolean status) throws SQLException {
        String query = "select * from customerinvoice where is_paid = " + status;
        try{
            showInvoices(query);
        } catch(SQLException s){
            throw new SQLException("Non sono state trovate ricevute con questo stato");
        }
    }
}
