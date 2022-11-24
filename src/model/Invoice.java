package model;

import java.math.BigDecimal;

public class Invoice {
    private int invoiceID;
    private BigDecimal invoice_amount;
    private boolean paid = false;

    //TODO aggiungere le righe d'ordine e relativo metodo per visualizzare?

    public Invoice(){}

    public Invoice(int invoiceID, BigDecimal amount){
        this.invoiceID = invoiceID;
        this.invoice_amount = amount;
    }

    public int getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
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
        return "Ricevuta dell'ordine #" + this.invoiceID + ": quota da pagare " + this.invoice_amount + "€, stato pagamento " + this.paid;
    }
}
