package BusinessLogic;

import DAO.*;
import model.*;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public abstract class BusinessLogic {
    /**
     * Metodo che mostra il menù principale del programma, permette di accedere ai metodi per eseguire operazioni su clienti o prenotazioni o chiudere il programma
     */
    public static void mainMenu() throws SQLException {
        UmbrellaTypeDAO utd = UmbrellaTypeDAO.getInstance();
        UmbrellaType uTable = utd.getUTypes(); //TODO da riallocare in una init()
        boolean running = true;
        while(running) {
            Scanner input = new Scanner(System.in);
            System.out.println("Scegli un'opzione:");
            //System.out.println("\t -");
            System.out.println("\t 1 - Operazioni Cliente");
            System.out.println("\t 2 - Operazioni Prenotazione");
            System.out.println("\t 3 - Operazioni Stabilimento");
            System.out.println("\t 4 - Esci");
            int choice;
            try{
                choice = input.nextInt();
            } catch (InputMismatchException i){
                choice = 0;

            }
            switch (choice) {
                case 1 -> customerOptions();
                case 2 -> {
                    System.out.println("-- PRENOTAZIONE --");
                    System.out.println("IN COSTRUZIONE...");
                }
                case 3 -> resortOptions();
                case 4 -> {
                    System.out.println("-- CHIUSURA PROGRAMMA --");
                    running = false;
                }
                default -> System.err.println("Opzione non valida...");
            }
        }
    }

    /**
     * Metodo che mostra un sotto-menù con le operazioni eseguibili riguardo a un cliente
     */
    private static void customerOptions(){
        boolean cRunning = true;
        while(cRunning) {
            Scanner cInput = new Scanner(System.in);
            System.out.println("\t-- CLIENTE --");
            System.out.println("\t 1 - Aggiungi nuovo cliente");
            System.out.println("\t 2 - Trova e modifica cliente");
            System.out.println("\t 3 - Torna indietro");
            int choice;
            try{
                choice = cInput.nextInt();
            } catch(InputMismatchException i){
                choice = 0;
            }
            switch(choice) {
                case 1 -> addNewCustomer();
                case 2 -> findCustomer();
                case 3 -> {
                    System.out.println("Torna a pagina precedente...");
                    cRunning = false;
                }
                default -> System.err.println("Opzione non valida...");
            }
        }
    }

    /**
     * Metodo che invoca i metodi di Customer e CustomerDAO per aggiungere un nuovo cliente al database
     */
    private static void addNewCustomer() {
        CustomerDAO cd = CustomerDAO.getINSTANCE(); //TODO qui va la factory
        Customer newC = Customer.createNewCustomer();
        cd.addNewCustomer(newC);
    }

    /**
     * Metodo che permette di scegliere come eseguire la ricerca di un cliente nel database; se il cliente è presente nel database, invoca anche il metodo che permette di modificarne i campi
     */
    private static void findCustomer() {
        boolean findCRunning = true;
        while(findCRunning) {
            System.out.println("\t Cercare per:");
            System.out.println("\t 1 - Codice cliente");
            System.out.println("\t 2 - Dati cliente");
            System.out.println("\t 3 - Torna indietro");
            Scanner findC_input = new Scanner(System.in);
            Scanner customerData;
            CustomerDAO cd = CustomerDAO.getINSTANCE();
            int choice;
            try{
                choice = findC_input.nextInt();
            } catch(InputMismatchException i){
                choice = 0;
            }
            switch (choice) {
                case 1 -> {
                    boolean notACNumber = true;
                    int choiceId = 0;
                    while(notACNumber){
                        System.out.println("Inserire codice cliente:");
                        customerData = new Scanner(System.in);
                        try{
                            choiceId = customerData.nextInt();
                            notACNumber = false;
                        } catch(InputMismatchException i){
                            System.err.println("Inserire un codice cliente valido...");
                        }
                    }
                    Customer c = new Customer();
                    try{
                        c = cd.findById(choiceId);
                    } catch (SQLException s){
                        System.err.println(s.getMessage());
                    }
                    if(c.get_first_name() != null) { // non permette di modificare il cliente se non lo trova nel database
                        //TODO aggiungere un nuovo sotto-menù dove sono mostrate le opzioni che possono essere scelte: modifica info cliente, crea nuova prenotazione, modifica prenotazione, cancella prenotazione...
                        modifyClientInfo(c);
                        cd.updateInfo(c); //TODO forse deve essere chiamato in modifyClientInfo, nell'ultima opzione...
                    }
                }
                case 2 -> {
                    boolean nameNotValid = true;
                    String fullName = "";
                    while(nameNotValid){
                        System.out.println("Inserire dati cliente (formato: Nome Cognome):");
                        customerData = new Scanner(System.in);
                        fullName = customerData.nextLine();
                        if(fullName.matches("^[A-Z][a-z]+\\s[A-Z][a-z]+$")){ //controlla che le credenziali vengano passate nel formato corretto; NB non sono accettati nomi o cognomi composti da una sola lettera
                            nameNotValid = false;
                        } else {
                            System.err.println("Formato nome non valido...");
                        }
                    }
                    boolean emailNotValid = true;
                    String email = "";
                    while(emailNotValid){
                        System.out.println("Inserire dati cliente (e-mail):");
                        customerData = new Scanner(System.in);
                        email = customerData.nextLine();
                        if(email.matches("^(.+)@(.+).(.+)$")){
                            emailNotValid = false;
                        }
                        else{
                            System.err.println("Indirizzo e-mail non valido...");
                        }
                    }
                    Customer c = cd.findByInfo(fullName, email);
                    if(c.get_customerID() != 0) { // non permette di modificare il cliente se non lo trova nel database
                        modifyClientInfo(c);
                        cd.updateInfo(c);
                    }
                }
                case 3 -> {
                    System.out.println("Torna a pagina precedente...");
                    findCRunning = false;
                }
                default -> System.err.println("Opzione non valida...");
            }
        }
    }

    private static void addNewReservation(int customerId) throws SQLException {
        ReservationDAO rd = ReservationDAO.getInstance();
        Reservation newRes = Reservation.createNewReservation(customerId);
        //TODO decidere se inserire le righe di codice che mostrano i tipi di ombrellone in un metodo a parte
        UmbrellaTypeDAO uTD = UmbrellaTypeDAO.getInstance(); //Le righe successive mostrano a schermo il tipo di ombrellone che si può scegliere
        UmbrellaType umbrellaTable = uTD.getUTypes();
        System.out.println("Seleziona il tipo di ombrellone:");
        for(Map.Entry<Integer, TypeDetails> type: umbrellaTable.getUTypeMap().entrySet()){ //cicla sugli elementi di umbrellaTable e li printa a schermo
            System.out.println("\t" + type.getKey() + " - " + type.getValue().getTypeName());
        }
        //TODO input del tipo di ombrellone
        //TODO aggiungere un metodo per la ricerca di ombrelloni liberi in una certa data: permettere di ricercare ombrelloni di un certo tipo e aggiungerlo alla prenotazione appena creata
        //TODO metodo per scegliere l'ombrellone preferito dalla lista di Ombrelloni ritornata dal metodo precedente
    }

    /**
     * Metodo che permette di modificare i campi di un oggetto Customer c che è stato trovato nel database attraverso i metodi CustomerDAO.findById() o CustomerDAO.findByInfo()
     * @param c è il Customer di cui si vuole modificare le informazioni e che viene poi restituito
     */
    //TODO [PROBABILMENTE SI PUO' USARE CUSTOMERDAO.FINDHOMONYM()] aggiungere il codice che spiega che, nel caso in cui il Customer c appena modificato sia identico ad uno già presente nel database, la modifica dei dati del suddetto c non è possibile e quindi verranno mantenuti i dati precedenti
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
                System.out.println("\t 4 - Termina modifiche");
                input = new Scanner(System.in);
                int choice;
                try{
                    choice = input.nextInt();
                } catch(InputMismatchException i){
                    choice = 0;
                }
                switch (choice) {
                    case 1 -> {
                        System.out.println("Inserire nuovo nome:");
                        input = new Scanner(System.in);
                        c.set_first_name(input.nextLine());
                    }
                    case 2 -> {
                        System.out.println("Inserire nuovo cognome:");
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
                                System.err.println(e.getMessage());
                            }
                        }
                    }
                    case 4 -> modifying = false;
                    default -> System.err.println("Opzione non valida...");
                }
            }
        }
    }

    /**
     * Metodo che permette di accedere a un sotto-menù con operazioni di utility per la gestione del bagno (e.g. operazioni di visualizzazione)
     */
    private static void resortOptions(){
        boolean oRunning = true;
        while(oRunning){
            System.out.println("Selezionare l'operazione che si vuole compiere: ");
            System.out.println("\t 1 - Ricerca per clienti" );
            System.out.println("\t 2 - Ricerca per ombrelloni");
            System.out.println("\t 3 - Ricerca per prenotazioni attive");
            System.out.println("\t 4 - Ricerca per pagamenti");
            System.out.println("\t 5 - Torna indietro");
            Scanner input = new Scanner(System.in);
            int choice;
            try{
                choice = input.nextInt();
            } catch(InputMismatchException i){
                choice = 0;
            }
            switch(choice) {
                case 1 -> clientSearch();
                case 2 -> umbrellaSearch();
                case 3 -> reservationSearch();
                case 4 -> paymentSearch();
                case 5 -> oRunning = false;
                default -> System.err.println("Opzione non valida...");
            }

        }
    }

    /**
     * Metodo che permette da accedere a un sotto-menù con operazioni che permettono di visualizzare a schermo i clienti
     * registrati al bagno secondo criteri selezionabili.
     */
    private static void clientSearch(){
        boolean search = true;
        CustomerDAO cd = CustomerDAO.getINSTANCE();
        int choice;
        while(search){
            System.out.println("Ricerca per: ");
            System.out.println("\t 1 - ID cliente");
            System.out.println("\t 2 - Nome e Cognome");
            System.out.println("\t 3 - Nome");
            System.out.println("\t 4 - Cognome");
            System.out.println("\t 5 - Email");
            System.out.println("\t 6 - Torna indietro");
            Scanner option = new Scanner(System.in);
            try{
                choice = option.nextInt();
            } catch(InputMismatchException i){
                choice = 0;
            }
            Scanner customerData;
            switch(choice){
                case 1 -> { //TODO serve?
                    System.out.println("Inserire codice cliente:");
                    customerData = new Scanner(System.in);
                    try{
                        System.out.println(cd.findById(customerData.nextInt()));
                    } catch(InputMismatchException i){
                        System.err.println("Inserire un codice cliente...");
                    } catch(SQLException s){
                        System.err.println(s.getMessage());
                    }
                }
                case 2 -> {
                    System.out.println("Inserire nome e cognome del cliente da cercare (formato: Nome Cognome):");
                    customerData = new Scanner(System.in);
                    String name = customerData.nextLine();
                    if(name.matches("^[A-Z][a-z]+\\s[A-Z][a-z]+$")) {
                        String[] fullName = name.split(" ");
                        cd.findByFullName(fullName);
                    } else {
                        System.err.println("Formato nome non valido...");
                    }
                }
                case 3 -> {
                    System.out.println("Inserire nome del cliente: ");
                    customerData = new Scanner(System.in);
                    String name = customerData.nextLine();
                    if(name.matches("^[A-Z][a-z]+$")){
                        cd.findByFirstName(name);
                    } else {
                        System.err.println("Formato nome non valido...");
                    }

                }
                case 4 -> {
                    System.out.println("Inserire cognome del cliente: ");
                    customerData = new Scanner(System.in);
                    String surname = customerData.nextLine();
                    if(surname.matches("^[A-Z][a-z]+$")){
                        cd.findByLastName(surname);
                    } else {
                        System.err.println("Formato cognome non valido...");
                    }
                }
                case 5 -> {
                    System.out.println("Inserire e-mail del cliente: ");
                    customerData = new Scanner(System.in);
                    String email = customerData.nextLine();
                    if(email.matches("^(.+)@(.+).(.+)$")){
                        cd.findByEMail(email);
                    } else {
                        System.err.println("Formato indirizzo e-mail non valido...");
                    }
                }
                case 6 -> search = false;
                default -> System.err.println("Opzione non valida...");

            }
        }
    }

    /**
     * Metodo che permette di accedere a un sotto-menù con operazioni che permettono di visualizzare a schermo gli ombrelloni
     * in possesso del bagno secondo criteri selezionabili.
     */
    private static void umbrellaSearch(){
        boolean search = true;
        UmbrellaDAO ud = UmbrellaDAO.getINSTANCE();
        int choice;
        while(search){
            System.out.println("Ricerca per: ");
            System.out.println("\t 1 - ID ombrellone");
            System.out.println("\t 2 - Tipo ombrellone");
            System.out.println("\t 3 - Torna indietro");
            Scanner option = new Scanner(System.in);
            try{
                choice = option.nextInt();
            } catch(InputMismatchException i){
                choice = 0;
            }
            Scanner umbrellaData;
            switch (choice){
                case 1 -> {
                    System.out.println("Inserire codice ombrellone: ");
                    umbrellaData = new Scanner(System.in);
                    try {
                        ud.findById(umbrellaData.nextInt());
                    } catch (InputMismatchException i){
                        System.err.println("Inserire un codice numerico...");
                    } catch (SQLException s){
                        System.err.println(s.getMessage());
                    }
                }
                case 2 -> {
                    System.out.println("Inserire codice tipo ombrellone: ");
                    umbrellaData = new Scanner(System.in);
                    try {
                        ud.findByType(umbrellaData.nextInt());
                    } catch (InputMismatchException i){
                        System.err.println("Codice ombrellone non valido...");
                    } catch (SQLException s){
                        System.err.println(s.getMessage());
                    }
                }
                case 3 -> search = false;
                default -> System.err.println("Opzione non valida...");
            }
        }
    }

    private static void reservationSearch(){
        //TODO
    }

    private static void paymentSearch(){
        boolean pRunning = true;
        InvoiceDAO id = InvoiceDAO.getINSTANCE();
        while(pRunning){
            System.out.println("Ricerca per: ");
            System.out.println("\t 1 - Ricerca per codice ricevuta" );
            System.out.println("\t 2 - Ricerca per codice cliente");
            System.out.println("\t 3 - Ricerca per stato pagamento");
            System.out.println("\t 4 - Torna indietro");
            Scanner input = new Scanner(System.in);
            int choice;
            try{
                choice = input.nextInt();
            } catch(InputMismatchException i){
                choice = 0;
            }
            switch(choice) {
                case 1 -> {
                    System.out.println("Inserire codice ricevuta/prenotazione: ");
                    input = new Scanner(System.in);
                    try{
                        id.findByInvoiceID(input.nextInt());
                    } catch (InputMismatchException i){
                        System.err.println("Inserire un codice numerico...");
                    } catch(SQLException s){
                        System.err.println(s.getMessage());
                    }
                }
                case 2 -> {
                    System.out.println("Inserire codice cliente: ");
                    input = new Scanner(System.in);
                    try{
                        id.findByCustomerID(input.nextInt());
                    } catch (InputMismatchException i){
                        System.err.println("Inserire un codice numerico...");
                    } catch(SQLException s){
                        System.err.println(s.getMessage());
                    }
                }
                case 3 -> {
                    System.out.println("Inserire stato pagamento (true o false): ");
                    input = new Scanner(System.in);
                    try{
                        id.findByPaymentStatus(input.nextBoolean());
                    } catch (InputMismatchException i){
                        System.err.println("Inserire un valore valido...");
                    } catch(SQLException s){
                        System.err.println(s.getMessage());
                    }
                }
                case 4 -> pRunning = false;
                default -> System.err.println("Opzione non valida...");
            }

        }
    }
}
