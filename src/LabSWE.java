import BusinessLogic.BusinessLogic;

public class LabSWE {
    public static void main(String[] args){
        BusinessLogic.mainMenu();
    }
        /*
        UmbrellaTypeDAO uTD = UmbrellaTypeDAO.getInstance();
        UmbrellaType umbrellaTable = uTD.getUTypes();
        try {
            Umbrella u = UmbrellaDAO.getINSTANCE().getUmbrella(2);
            Umbrella u2 = UmbrellaDAO.getINSTANCE().getUmbrella(5);
            Umbrella u3 = UmbrellaDAO.getINSTANCE().getUmbrella(10);
        } catch (SQLException e){
            System.out.println("L'ombrellone non è salvato in memoria...");
        }
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
                    CustomerDAO cd = CustomerDAO.getINSTANCE(); //TODO va messo in un try? la connessione potrebbe fallire...
                    input = new Scanner(System.in);
                    switch (input.nextInt()) {
                        case 1 -> cd.addNewCustomer();
                        case 2 -> {
                            System.out.println("\t Cercare per:");
                            System.out.println("\t 1 - Codice cliente");
                            System.out.println("\t 2 - Dati cliente");
                            System.out.println("\t 3 - Torna indietro");
                            input = new Scanner(System.in);
                            switch (input.nextInt()) {
                                case 1 -> {
                                    System.out.println("Inserire codice cliente:");
                                    input = new Scanner(System.in);
                                    Customer c = cd.findById(input.nextInt());
                                    if(c.get_first_name() != null) { // non permette di modificare il cliente se non lo trova nel database
                                        clientOptions(c);
                                        cd.updateCustomerInfo(c);
                                    }
                                }
                                case 2 -> {
                                    System.out.println("Inserire dati cliente (Nome Cognome):");
                                    input = new Scanner(System.in);
                                    String fullName = input.nextLine();
                                    System.out.println("Inserire dati cliente (e-mail):");
                                    input = new Scanner(System.in);
                                    String email = input.nextLine();
                                    Customer c = cd.findByInfo(fullName, email);
                                    if(c.get_customerID() != 0) { // non permette di modificare il cliente se non lo trova nel database
                                        clientOptions(c);
                                        cd.updateCustomerInfo(c);
                                    }
                                }
                                case 3 -> {
                                    System.out.println("SIUUUUM");
                                    //TODO forse per tornare alla pagina precedente serve un booleano
                                }
                                default -> System.out.println("Opzione non valida...");
                            }
                        }
                        case 3 -> {
                            System.out.println("Torna a pagina precedente...");
                            //TODO forse per tornare alla pagina precedente serve un booleano
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

         */

    /**
     * Permette di modificare le informazioni di un Customer
     * @param c Il Customer da modificare
     */
    /*
    private static void clientOptions(Customer c) {
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
                        while(!mailIsValid) {
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

     */
}

