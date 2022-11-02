package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class Reservation {
    private int reservationId;
    private int customerId;
    private int ombrelloneId; // TODO serve la classe ombrellone_prenotato?
    private LocalDate start_date;
    private LocalDate end_date;
    private BigDecimal total_price;
    private float discount_percent = 0;

    public Reservation(){

    }

    /**
     * Ritorna una Reservation in cui sono stati inseriti il codice cliente del Customer che l'ha richiesta e le date di inizio e fine prenotazione. L'ombrellone viene inserito successivamente nella BusinessLogic
     * @param customerId il codice del Customer che ha richiesto la Reservation
     * @return Reservation in cui sono inizializzati i parametri customerId, start_date e end_date
     */
    public static Reservation createNewReservation(int customerId){
        Reservation r = new Reservation();
        r.setCustomerId(customerId);
        r.setStart_date();
        r.setEnd_date();
        //completeMissingAttributes(r);
        return r;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setOmbrelloneId(int ombrelloneId) {
        this.ombrelloneId = ombrelloneId;
    }

    public void setTotal_price(BigDecimal total_price) {
        this.total_price = total_price;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getOmbrelloneId() {
        return ombrelloneId;
    }

    public BigDecimal getTotal_price() {
        return total_price;
    }

    public int getReservationId() {
        return reservationId;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public Date getSQLStart_date() {
        return Date.valueOf(start_date);
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public Date getSQLEnd_date() {
        return Date.valueOf(end_date);
    }

    private void setStart_date(){
        boolean validStartDate = false;
        while(!validStartDate) {
            System.out.println("Inserire data di inizio: (dd-mm-yyyy)");
            try {
                LocalDate tmp = set_date();
                if (tmp.compareTo(LocalDate.now()) >= 0) {
                    this.start_date = tmp;
                    validStartDate = true;
                } else {
                    System.err.println("La data inserita è precedente alla giornata odierna...");
                }
            } catch (NumberFormatException | DateTimeException | ArrayIndexOutOfBoundsException e){
                System.err.println(e.getMessage());
            }
        }
    }

    private void setEnd_date(){
        boolean validEndDate = false;
        while(!validEndDate) {
            System.out.println("Inserire data di fine: (dd-mm-yyyy)");
            try {
                LocalDate tmp = set_date();
                if (tmp.compareTo(this.start_date) >= 0) {
                    this.end_date = tmp;
                    validEndDate = true;
                } else {
                    System.err.println("La data inserita è precedente alla data di inizio prenotazione...");
                }
            } catch (NumberFormatException | DateTimeException | ArrayIndexOutOfBoundsException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private LocalDate set_date() {
        Scanner mySc = new Scanner(System.in);
        String date = mySc.nextLine();
        String[] fullDate = date.split("-");
        LocalDate localDate;

        try {
            int dayOfMonth = Integer.parseInt(fullDate[0]);
            int month = Integer.parseInt(fullDate[1]);
            int year = Integer.parseInt(fullDate[2]);
            localDate = LocalDate.of(year, month, dayOfMonth);
        } catch (NumberFormatException n) {
            throw new NumberFormatException("Inserire valori numerici...");
        } catch (DateTimeException d) {
            throw new DateTimeException("La data " + date + " non è valida...");
        } catch (ArrayIndexOutOfBoundsException a){
            throw new ArrayIndexOutOfBoundsException("Inserire tutti i parametri nel formato dd-mm-yyyy...");
        }
        return localDate;
    }

    @Override
    public String toString() {
        return "Prenotazione #" + this.reservationId + " del cliente #" + this.customerId + ".\nPrenotato l'ombrellone " +
                "#" + this.ombrelloneId + " nelle date dal " + this.start_date + " al " + this.end_date + ". Prezzo totale: " +
                this.total_price + "€.";
    }
}
