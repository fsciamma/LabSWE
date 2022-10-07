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

    public Reservation(int reservationID, int customerID, int ombrelloneID, Date start_date, Date end_date) {
        this.reservationID = reservationID;
        this.customerID = customerID;
        this.ombrelloneID = ombrelloneID;
        this.start_date = start_date;
        this.end_date = end_date;
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
