package BusinessLogic;

import DAO.CustomerDAO;
import model.Customer;

import java.sql.SQLException;
import java.util.Scanner;

public abstract class BusinessLogic {

    public static void mainMenu() {
        boolean running = true;
        while(running) {
            System.out.println("Scegli un'opzione:");
            //System.out.println("\t -");
            System.out.println("\t 1 - Operazioni Cliente");
            System.out.println("\t 2 - Operazioni Prenotazione");
            System.out.println("\t 3 - Esci");
            Scanner input = new Scanner(System.in);
            switch (input.nextInt()) {
                case 1 -> customerOptions();
                case 2 -> System.out.println("-- PRENOTAZIONE --");
                case 3 -> {
                    System.out.println("-- CHIUSURA PROGRAMMA --");
                    running = false;
                }
                default -> System.out.println("Opzione non valida...");
            }
        }
    }

    private static void customerOptions(){
        boolean cRunning = true;
        while(cRunning) {
            System.out.println("\t-- CLIENTE --");
            System.out.println("\t 1 - Aggiungi nuovo cliente");
            System.out.println("\t 2 - Trova e modifica cliente");
            System.out.println("\t 3 - Torna indietro");
            Scanner cInput = new Scanner(System.in);
            switch(cInput.nextInt()) {
                case 1 -> addNewCustomer();
                case 2 -> findCustomer();
                case 3 -> {
                    System.out.println("Torna a pagina precedente...");
                    cRunning = false;
                }
                default -> System.out.println("Opzione non valida...");
            }
        }
    }

    private static void addNewCustomer() {
        CustomerDAO cd = CustomerDAO.getINSTANCE();
        Customer newC = Customer.createNewCustomer();
        try {
            cd.addNewCustomer(newC);
        } catch (SQLException e) {
            System.out.println("Fratm esiste già questo cliente");
        }
    }

    private static void findCustomer() {
        boolean findCRunning = true;
        while(findCRunning) {
            System.out.println("\t Cercare per:");
            System.out.println("\t 1 - Codice cliente");
            System.out.println("\t 2 - Dati cliente");
            System.out.println("\t 3 - Torna indietro");
            CustomerDAO cd = CustomerDAO.getINSTANCE();
            Scanner findC_input = new Scanner(System.in);
            switch (findC_input.nextInt()) {
                case 1 -> {
                    System.out.println("Inserire codice cliente:");
                    Scanner customerNumber = new Scanner(System.in);
                    Customer c = cd.findById(customerNumber.nextInt());
                    if(c.get_first_name() != null) { // non permette di modificare il cliente se non lo trova nel database
                        modifyClientInfo(c);
                        cd.updateCustomerInfo(c);
                    }
                }
                case 2 -> {
                    System.out.println("Inserire dati cliente (Nome Cognome):");
                    Scanner customerInfo = new Scanner(System.in);
                    String fullName = customerInfo.nextLine();
                    System.out.println("Inserire dati cliente (e-mail):");
                    customerInfo = new Scanner(System.in);
                    String email = customerInfo.nextLine(); //TODO aggiungi controllo sulla mail
                    Customer c = cd.findByInfo(fullName, email);
                    if(c.get_customerID() != 0) { // non permette di modificare il cliente se non lo trova nel database
                        modifyClientInfo(c);
                        cd.updateCustomerInfo(c);
                    }
                }
                case 3 -> {
                    System.out.println("Torna a pagina precedente...");
                    findCRunning = false;
                }
                default -> System.out.println("Opzione non valida...");
            }
        }
    }

    private static void modifyClientInfo(Customer c) {
        System.out.println("Vuoi eseguire delle modifiche al cliente #" + c.get_customerID() + "? (Y/N)");
        Scanner input = new Scanner(System.in);
        String line = input.nextLine();
        if ("Y".equals(line) || "y".equals(line)) {  // Se viene inserito qualsiasi altro carattere esce dal metodo, terminando la modifica
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
                        boolean mailIsValid = false;
                        while (!mailIsValid) {
                            try {
                                c.set_email();
                                mailIsValid = true;
                            } catch (IllegalArgumentException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                    case 4 -> modifying = false;
                }
            }
        }
    }
}
