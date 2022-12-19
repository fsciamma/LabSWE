package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

public class Reservation {
    private int reservationID;
    private String customer;
    private ArrayList<ReservedAsset> reserved_assets;
    private Invoice invoice;
    private BigDecimal price = BigDecimal.ZERO;

    public Reservation(){
        this.reserved_assets = new ArrayList<>();
    }

    /**
     * Ritorna una Reservation in cui sono stati inseriti il codice cliente del Customer che l'ha richiesta e le date di inizio e fine prenotazione. L'ombrellone viene inserito successivamente nella BusinessLogic
     *
     * @param customerEmail@return Reservation in cui sono inizializzati i parametri customerId, start_date e end_date
     */
    public static Reservation createNewReservation(String customerEmail){
        Reservation res = new Reservation();
        res.setCustomer(customerEmail);
        return res;
    }

    public void compute_total() {
        for(ReservedAsset r : reserved_assets){
            this.price = this.price.add(r.getPrice());
        }
    }

    public BigDecimal getTotal_price() {
        return price;
    }

    public void setReservationId(int reservationId) {
        this.reservationID = reservationId;
    }

    public int getReservationId() {
        return reservationID;
    }

    public void setCustomer(String email) {this.customer = email;}

    public String getCustomer() {
        return customer;
    }

    public void addReservedAsset(ReservedAsset r) {
        this.reserved_assets.add(r);
    }

    public ArrayList<ReservedAsset> getReserved_assets(){
        return reserved_assets;
    }

    public LocalDate getNearestAssetDate(){
        LocalDate nearest = LocalDate.MAX;
        for(ReservedAsset r: this.reserved_assets){
            if(r.getStart_date().compareTo(nearest) < 0){
                nearest = r.getStart_date();
            }
        }
        return nearest;
    }

    @Override
    public String toString() {
        return "Prenotazione #" + this.reservationID + " del cliente #" + this.customer + ".\nPrezzo totale: " +
                this.price + "€.";
    }

    public void setReserved_Assets(ArrayList<ReservedAsset> assetList) {
        this.reserved_assets = assetList;
    }
}
