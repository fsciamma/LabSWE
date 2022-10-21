package DAO;

import model.Invoice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InvoiceDAO extends BaseDAO{
    private static InvoiceDAO INSTANCE;
    private InvoiceDAO(){}

    public static InvoiceDAO getINSTANCE() {
        if(INSTANCE == null){
            INSTANCE = new InvoiceDAO();
        }
        return INSTANCE;
    }

    //TODO Rielaborare questi metodi secondo il nuovo schema
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
        System.out.println(i);
        return i;
    }

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

    public void findByInvoiceID(int id) throws SQLException{
        Invoice i = new Invoice();
        String query = "select * from customerinvoice where invoiceid = " + id;
        try{
            getInvoice(query, i);
        } catch(SQLException s){
            throw new SQLException("La ricevuta " + id + " non è stata trovata");
        }
    }
}
