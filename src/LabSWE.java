import DAO.CustomerDAO;

import java.sql.SQLException;
import java.util.Scanner;


public class LabSWE {
    public static void main(String[] args) throws SQLException{
        boolean running = true;
        while(running) {
            System.out.println("Scegli un'opzione:");
            //System.out.println("\t -");
            System.out.println("\t 1 - Operazioni Cliente");
            System.out.println("\t 2 - Operazioni Prenotazione");
            System.out.println("\t 3 - Esci");
            Scanner input = new Scanner(System.in);
            switch (input.nextInt()) {
                case 1 -> {
                    System.out.println("\t-- CLIENTE --");
                    System.out.println("\t 1 - Aggiungi nuovo cliente");
                    System.out.println("\t 2 - Modifica dati cliente");
                    System.out.println("\t 3 - Torna indietro");
                    CustomerDAO cd = new CustomerDAO();
                    input = new Scanner(System.in);
                    switch (input.nextInt()) {
                        case 1 -> cd.addNewCustomer();
                        case 2 -> {
                            System.out.println("\t Cercare per:");
                            System.out.println("\t 1 - codice cliente");
                            System.out.println("\t 2 - dati cliente");
                            System.out.println("\t 3 - Torna indietro");
                            input = new Scanner(System.in);
                            switch (input.nextInt()) {
                                case 1 -> {
                                    System.out.println("Inserire codice cliente:");
                                    input = new Scanner(System.in);
                                    cd.updateCustomerInfo(input.nextInt());
                                }
                                case 2 -> {
                                    System.out.println("Inserire dati cliente:");
                                    input = new Scanner(System.in);
                                    cd.updateCustomerInfo(input.nextLine());
                                }
                                case 3 -> {
                                }
                                default -> System.out.println("Opzione non valida...");
                            }
                        }
                        default -> System.out.println("Opzione non valida...");
                    }
                }
                case 2 -> System.out.println("-- PRENOTAZIONE --");
                case 3 -> {
                    System.out.println("-- CHIUSURA PROGRAMMA --");
                    running = false;
                }
                default -> System.out.println("Opzione non valida...");
            }
            System.out.println("\n");
        }

    }
}

