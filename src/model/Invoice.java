package model;

import java.math.BigDecimal;

public class Invoice {
    private int invoiceID;
    private int customerID;
    private BigDecimal invoice_amount; //corrisponde a total_price * (100 - discount_percentage) indicati nella Reservation corrispondente
    private boolean paid = false;

    public Invoice(){}

    public Invoice(int invoiceID, int customerID) {
        this.invoiceID = invoiceID;
        this.customerID = customerID;
    }

    public int getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public BigDecimal getInvoice_amount() {
        return invoice_amount;
    }

    public void setInvoice_amount(BigDecimal invoice_amount) {
        this.invoice_amount = invoice_amount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public String toString() {
        return "Ricevuta dell'ordine #" + this.invoiceID + ": cliente #" + this.customerID + ", quota da pagare " + this.invoice_amount + "€, stato pagamento " + this.paid;
    }
}
