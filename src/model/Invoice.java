package model;

public class Invoice {
    private int invoiceID;
    private int customerID;
    private float invoice_amount; //corrisponde a total_price * (100 - discount_percentage) indicati nella Reservation corrispondente
    private boolean paid = false;

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

    public float getInvoice_amount() {
        return invoice_amount;
    }

    public void setInvoice_amount(float invoice_amount) {
        this.invoice_amount = invoice_amount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
