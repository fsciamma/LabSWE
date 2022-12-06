package model;

import DAO.UmbrellaDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;

import java.util.Scanner;

public class Reservation {
    private int reservationID;
    private String customer;
    private ArrayList<ReservableAsset> reserved_assets;
    private int invoiceID;
    private BigDecimal price;

    public Reservation(){
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


    ///**
    // * Modifica la Reservation che prende in ingresso aggiungendo dei valori ai campi ombrelloneid e total_price se è presente un ombrellone disponibile per le date richieste
    // * @param res La Reservation da modificare
    // */
    //public static void completeMissingAttributes(Reservation res){
    //    Umbrella u = new Umbrella();
    //    int favoriteType;
    //    boolean available = false;
    //    while(!available) {
    //        if(UmbrellaDAO.getINSTANCE().showAvailableUmbrellas(res.start_date, res.end_date)){ //NB se la view availableUmbrellas è vuota, non esegue il corpo
    //            favoriteType = getFavoriteType();
    //            u = getAvailableUmbrella(favoriteType);
    //            available = true;
    //        } else {
    //            throw new RuntimeException("Spiacente! Non ci sono ombrelloni disponibili per le date selezionate!");
    //        }
    //    }
    //    res.setOmbrelloneId(u.getUmbrellaId());
    //    res.setTotal_price(BigDecimal.valueOf(u.getDaily_price() * (DAYS.between(res.start_date, res.end_date) + 1)));
    //}

    /**
     * Metodo che utilizza il relativo metodo di UmbrellaDAO per selezionare un ombrellone da inserire nella prenotazione,
     * con la possibilità di filtrare la scelta anche in base a un particlare tipo di ombrellone.
     * @param favoriteType: intero rappresentate un tipo scelto come preferito per filtrare i risultati
     * @return Umbrella: l'ombrellone selezionato da inserire nella prenotazione
     */
    private static Umbrella getAvailableUmbrella(int favoriteType){
        UmbrellaDAO ud = UmbrellaDAO.getINSTANCE();
        Umbrella u = new Umbrella();
        ArrayList<Integer> availableUmbrellas = ud.getAvailableUmbrellas(favoriteType);
        try{
            System.out.println("Per favore, seleziona uno degli ombrelloni disponibili.");
            boolean notValidNumber = true;
            int number;
            while(notValidNumber){
                Scanner input = new Scanner(System.in);
                try{
                    number = input.nextInt();
                } catch (InputMismatchException i){
                    number = 0;
                }
                if(availableUmbrellas.contains(number)){
                    u = ud.findById(number);
                    notValidNumber = false;
                } else {
                    System.err.println("Ombrellone " + number + " non disponibile. Prego selezionare un altro numero.");
                }
            }
        }catch (SQLException e){
            System.err.println(e.getMessage());
        }
        return u;
    }

    /**
     * Metodo che viene usato per selezionare un tipo di ombrellone per filtrare la ricerca
     * @return int : intero rappresentante il tipo selezionato
     */
    private static int getFavoriteType() {
        int favoriteType;
        try{
            favoriteType = new Scanner(System.in).nextInt();
            System.out.println("E' stato richiesto un ombrellone del tipo: " + UmbrellaType.getInstance().getUTypeMap().get(favoriteType).getTypeName());
        } catch (InputMismatchException | NullPointerException i){
            favoriteType = 0;
            System.out.println("Nessun tipo specifico richiesto.");
        }
        return favoriteType;
    }

    public void setTotal_price(BigDecimal total_price) {
        this.price = total_price;
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

    public void updateReservation(ReservableAsset r, int days){
        this.reserved_assets.add(r);
        setTotal_price(getTotal_price().add(r.getPrice().multiply(BigDecimal.valueOf(days))));
    }

    public ArrayList<ReservableAsset> getReserved_assets(){
        return reserved_assets;
    }

    @Override
    public String toString() {
        return "Prenotazione #" + this.reservationID + " del cliente #" + this.customer + ".\nPrezzo totale: " +
                this.price + "€.";
    }
}
