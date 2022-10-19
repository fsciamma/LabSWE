package model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Scanner;

public class Reservation {
    private int reservationId;
    private int customerId;
    private int ombrelloneId; // TODO serve la classe ombrellone_prenotato?
    private LocalDate start_date;
    private LocalDate end_date;
    private float total_price;
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
        boolean validStartDate = false;
        while(!validStartDate) {
            try {
                r.setStart_date();
                validStartDate = true;
            } catch (NumberFormatException | DateTimeException e){
                System.err.println(e.getMessage());
            }
        }
        boolean validEndDate = false;
        while(!validEndDate) {
            try {
                r.setEnd_date();
                if(r.end_date.compareTo(r.start_date) >= 0) { // controlla che la data di fine prenotazione sia successiva a quella d'inizio prenotazione
                    validEndDate = true;
                }
                else {
                    System.err.println("La data di fine prenotazione è precedente alla data di inizio prenotazione.");
                    System.out.println("Inserire una nuova data di fine prenotazione");
                }
            } catch (NumberFormatException | DateTimeException e){
                System.err.println(e.getMessage());
            }
        }
        return r;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }


    public void setStart_date() throws NumberFormatException, DateTimeException{
        Scanner mySc = new Scanner(System.in);
        System.out.println("Inserire data di inizio: (dd-mm-yyyy)");
        this.start_date = set_date(mySc);
    }

    public void setEnd_date() throws NumberFormatException, DateTimeException {
        Scanner mySc = new Scanner(System.in);
        System.out.println("Inserire data di fine: (dd-mm-yyyy)");
        this.end_date = set_date(mySc);
    }

    private LocalDate set_date(Scanner mySc) {
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
        }
        return localDate;
    }
}
