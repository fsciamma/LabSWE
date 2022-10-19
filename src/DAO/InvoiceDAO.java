package DAO;

import model.Invoice;

import java.math.BigDecimal;
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

    //TODO Rielaborare questi metodi seconod il nuovo schema
    public void addNewInvoice(Invoice i){
        String query = "select * from customerinvoice";
        ResultSet rs;
        try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            rs = stmt.executeQuery(query);

            rs.moveToInsertRow();

            rs.updateInt("customerid", i.getCustomerID());
            rs.updateBigDecimal("invoice_amount", new BigDecimal(Float.toString(i.getInvoice_amount())));
            rs.updateBoolean("is_paid", i.isPaid());

            rs.insertRow();
            rs.beforeFirst();

        } catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }
}
