import DAO.CustomerDAO;
import model.Customer;

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
                    System.out.println("\t 2 - Trova e modifica cliente");
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
                                    Customer c = cd.findById(input.nextInt());
                                    clientOptions(c);
                                    //TODO inserire il codice per modificare il database in base ai nuovi valori contenuti in c: il metodo dovrà essere contenuto in CustomerDAO
                                }
                                case 2 -> {
                                    System.out.println("Inserire dati cliente:");
                                    input = new Scanner(System.in);
                                    String fullName = input.nextLine();
                                    input = new Scanner(System.in);
                                    String email = input.nextLine();
                                    Customer c = cd.findByInfo(fullName, email);
                                    clientOptions(c);
                                    //TODO inserire il codice per modificare il database in base ai nuovi valori contenuti in c: il metodo dovrà essere contenuto in CustomerDAO
                                }
                                case 3 -> {
                                    //TODO forse per tornare alla pagina precedente serve un booleano
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

    private static void clientOptions(Customer c) {
        System.out.println("Vuoi eseguire delle modifiche al cliente #" + c.get_customerID() + "? (Y/N)");
        Scanner input = new Scanner(System.in);
        String line = input.nextLine();
        if ("Y".equals(line) || "y".equals(line)) {
            boolean modifying = true;
            while (modifying) {
                System.out.println("Cosa vuoi modificare?");
                System.out.println("\t 1 - Nome");
                System.out.println("\t 2 - Cognome");
                System.out.println("\t 3 - Indirizzo e-mail");
                System.out.println("\t 4 - Torna indietro");
                input = new Scanner(System.in);
                switch (input.nextInt()) {
                    case 1 -> {
                        input = new Scanner(System.in);
                        c.set_first_name(input.nextLine());
                    }
                    case 2 -> {
                        input = new Scanner(System.in);
                        c.set_last_name(input.nextLine());
                    }
                    case 3 -> {
                        input = new Scanner(System.in);
                        c.set_email(input.nextLine());
                    }
                    case 4 -> modifying = false;
                }
            }
        }
    }
}

