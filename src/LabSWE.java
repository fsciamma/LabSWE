import DAO.CustomerDAO;

import java.sql.SQLException;
import java.util.Scanner;


public class LabSWE {
    public static void main(String[] args) throws SQLException{
        boolean running = true;
        while(running) {
            System.out.println("Scegli un'opzione:");
            //System.out.println("\t -");
            System.out.println("\t 1 - Aggiungi un nuovo cliente");
            System.out.println("\t 2 - Effettua una prenotazione");
            System.out.println("\t 3 - Esci");
            Scanner input = new Scanner(System.in);
            switch (input.nextInt()) {
                case 1 -> {
                    System.out.println("Nuovo cliente");
                    CustomerDAO cd = new CustomerDAO();
                    cd.addNewCustomer();
                }
                case 2 -> System.out.println("Nuova prenotazione");
                case 3 -> {
                    System.out.println("Chiudi programma");
                    running = false;
                }
                default -> System.out.println("Opzione non valida...");
            }
            System.out.println("\n");
        }

    }
}

