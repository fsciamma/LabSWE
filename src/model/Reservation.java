package model;

import java.util.Date;

public class Reservation {
    private int reservationID;
    private int customerID;
    private int ombrelloneID; // TODO serve la classe ombrellone_prenotato?
    private Date start_date;
    private Date end_date;
    private float total_price;
    private float discount_percent = 0;

    /**
     * Crea un oggetto prenotazione usando i parametri passati. Chiama un metodo per cercare gli ombrelloni liberi e ne
     * assegna uno alla prenotazione.
     *
     * @param customerID
     * @param start_date
     * @param end_date
     */
    public Reservation(int customerID, Date start_date, Date end_date) {
        this.customerID = customerID;
        this.start_date = start_date;
        this.end_date = end_date;
        //reservationID viene assegnato dal database: forse devo creare un altro costruttore che costruisce una
        //prenotazione da una riga del database...
        //TODO chiamare metodo per cercare un OmbrelloneLibero nelle date indicate e assegnarlo a this.OmbrelloneID
        //TODO aggiungere un try per creare una nuova riga nella tabella Reservation con i dati ricevuti: a questo punto alla prenotazione è assegnato anche un reservationID
    }

    public int getReservationID() {
        return reservationID;
    }

    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getOmbrelloneID() {
        return ombrelloneID;
    }

    public void setOmbrelloneID(int ombrelloneID) {
        this.ombrelloneID = ombrelloneID;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public float getTotal_price() {
        return total_price;
    }

    public void setTotal_price(float total_price) {
        this.total_price = total_price;
    }

    public float getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(float discount_percent) {
        this.discount_percent = discount_percent;
    }
}
