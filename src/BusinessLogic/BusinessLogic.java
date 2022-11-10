package BusinessLogic;

import DAO.*;
import model.*;

import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.time.temporal.ChronoUnit.DAYS;

public abstract class BusinessLogic {
    /**
     * Metodo che mostra il menù principale del programma, permette di accedere ai metodi per eseguire operazioni su clienti o prenotazioni o chiudere il programma
     */
    public static void mainMenu(){
        boolean running = true;
        while(running) {
            Scanner input = new Scanner(System.in);
            System.out.println("Selezionare un'opzione:");
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
    private static void addNewCustomer(){
        CustomerDAO cd = CustomerDAO.getINSTANCE(); //TODO qui va la factory
        Customer newC = Customer.createNewCustomer();
        cd.addNewCustomer(newC);
        System.out.println("Vuoi effettuare una prenotazione? (Y/N)");
        Scanner input = new Scanner(System.in);
        String line = input.nextLine();
        if ("Y".equals(line) || "y".equals(line)) { // Se viene inserito qualsiasi altro carattere esce dall'if
            try{
                newC = cd.findByInfo(newC.get_first_name() + " " + newC.get_last_name(), newC.get_email());
            } catch (SQLException s){
                System.err.println("A quanto pare il cliente non è stato salvato...");
            }
            addNewReservation(newC.get_customerID());
        } else {
            System.out.println("Non è stata aggiunta nessuna prenotazione.");
        }

    }

    /**
     * Metodo che permette di scegliere come eseguire la ricerca di un cliente nel database; se il cliente è presente nel database, invoca anche il metodo che permette di modificarne i campi
     */
    private static void findCustomer() {
        boolean findCRunning = true;
        Customer c = new Customer();
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
                    int choiceId;
                    while(notACNumber){
                        System.out.println("Inserire codice cliente:");
                        customerData = new Scanner(System.in);
                        try{
                            choiceId = customerData.nextInt();
                            notACNumber = false;
                            c = cd.findById(choiceId);
                        } catch(InputMismatchException i){
                            System.err.println("Inserire un codice cliente valido...");
                        } catch (SQLException s){
                            System.err.println(s.getMessage());
                        }
                    }
                }
                case 2 -> {
                    boolean nameNotValid = true;
                    String fullName = "";
                    while(nameNotValid){
                        System.out.println("Inserire dati cliente (formato: Nome Cognome):");
                        customerData = new Scanner(System.in);
                        fullName = customerData.nextLine();
                        if(fullName.matches("^([A-ZÀ-Ü^×]){1}([a-zà-ü^÷])*+\\s([A-ZÀ-Ü^×]){1}([a-zà-ü^÷])*+$")){ //controlla che le credenziali vengano passate nel formato corretto; NB non sono accettati nomi o cognomi composti da una sola lettera
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
                    try{
                        c = cd.findByInfo(fullName, email);
                    } catch (SQLException e){
                        System.err.println("Cliente non trovato: i dati inseriti non risultano nel database");
                    }
                }
                case 3 -> {
                    System.out.println("Torna a pagina precedente...");
                    findCRunning = false;
                }
                default -> System.err.println("Opzione non valida...");
            }
            if(c.get_customerID() != 0){
                customerMenu(c);
            }
        }
    }

    /**
     * Metodo che crea un'interfaccia e contiene la logica per navigare tra le opzioni attuabili sul customer in ingresso
     * @param c Customer appena trovato da findCustomer()
     */
    private static void customerMenu(Customer c) {
        boolean customer_menu = true;
        while(customer_menu) {
            System.out.println(c);
            System.out.println("Selezionare un'opzione:");
            System.out.println("\t1 - Aggiungi prenotazione"); //TODO aggiungere possibilità di modificare una prenotazione o cancellarla(volendo anche entro una certa data)
            System.out.println("\t2 - Vedi prenotazioni");
            System.out.println("\t3 - Modifica dati cliente");
            System.out.println("\t4 - Torna indietro");
            Scanner findC_menu_input = new Scanner(System.in);
            int customer_choice;
            try {
                customer_choice = findC_menu_input.nextInt();
            } catch (InputMismatchException i) {
                customer_choice = 0;
            }
            switch (customer_choice) {
                case 1 -> addNewReservation(c.get_customerID());
                case 2 -> showReservations(c.get_customerID());
                case 3 -> modifyClientInfo(c);
                case 4 -> {
                    System.out.println("Torna a pagina precedente...");
                    customer_menu = false;
                }
                default -> System.out.println("Opzione non valida...");
            }
            System.out.println();
        }
    }

    /**
     * Chiama findByCustomerId di ReservationDAO
     * @param customerID L'identificativo del Customer di cui si vogliono vedere le Reservation effettuate
     */
    private static void showReservations(int customerID){
        try {
            ReservationDAO.getInstance().findByCustomerId(customerID);
            reservationOptionMenu(customerID);
        } catch (SQLException s){
            System.err.println(s.getMessage());
        }
    }

    /**
     * Metodo che permette di scegliere quale operazione compiere su una delle Reservations legate al Customer
     * @param customerID Identificativo del Customer di cui sono state cercate le prenotazioni: viene usato per controllare che non venga modificata o cancellata una prenotazione che non gli appartiene
     */
    private static void reservationOptionMenu(int customerID) {
        boolean running = true;
        while(running) {
            System.out.println("Selezionare l'operazione che si vuole eseguire:");
            System.out.println("\t1 - Modifica una prenotazione");
            System.out.println("\t2 - Cancella una prenotazione");
            System.out.println("\t3 - Torna indietro");
            Scanner input = new Scanner(System.in);
            int choice = 0;
            try {
                choice = input.nextInt();
            } catch (InputMismatchException i) {
                System.err.println("Inserire un valore numerico");
            }
            switch (choice) {
                case 1 -> modifyReservation(customerID);
                case 2 -> deleteReservation(customerID);
                case 3 -> {
                    System.out.println("Torna a pagina precedente");
                    running = false;
                }
                default -> System.err.println("Opzione non valida...");
            }
        }
    }

    private static void modifyReservation(int customerID) {

    }

    /**
     * Permette di cancellare una Reservation attraverso l'invocazione del metodo ReservationDAO.deleteReservation se il periodo di annullamento di questa non è scaduto
     * @param customerID Viene usato per impedire che venga cancellata una prenotazione non appartenente al Customer che identifica
     */
    private static void deleteReservation(int customerID) {
        System.out.println("Inserire il codice della prenotazione da cancellare:");
        int resCode = 0;
        boolean notValidCode = true;
        while(notValidCode) {
            try {
                resCode = new Scanner(System.in).nextInt();
                notValidCode = false;
            } catch (InputMismatchException i) {
                System.err.println("Inserire un codice prenotazione...");
            }
        }
        try {
            Reservation res = ReservationDAO.getInstance().findById(resCode);
            //Controlla che il Customer che richiede la cancellazione sia anche lo stesso che possiede la prenotazione e che manchino almeno 7 giorni alla data d'inizio della prenotazione
            if(res.getCustomerId() == customerID && DAYS.between(LocalDate.now(), res.getStart_date()) >= 7) {
                ReservationDAO.getInstance().deleteReservation(resCode);
            } else {
                System.out.println("Non puoi cancellare questa prenotazione! Il periodo per annullare la prenotazione è scaduto!");
            }
        } catch (SQLException s){
            System.err.println(s.getMessage());
        }
    }

    /**
     * Metodo che permette di aggiungere una Reservation a nome di un cliente appena aggiunto al database oppure che è stato appena cercato
     * @param customerId ID del cliente che richiede una nuova prenotazione
     */
    private static void addNewReservation(int customerId){
        try {
            Reservation newRes = Reservation.createNewReservation(customerId);
            ReservationDAO rd = ReservationDAO.getInstance();
            rd.addNewReservation(newRes);
            System.out.println("Prenotazione effettuata!");
            //TODO aggiungere un metodo per poter aggiungere già ora degli extras
        } catch (RuntimeException r){
            System.out.println(r.getMessage());
        }
    }

    /**
     * Metodo che permette di modificare i campi di un oggetto Customer c che è stato trovato nel database attraverso i metodi CustomerDAO.findById() o CustomerDAO.findByInfo()
     * @param c è il Customer di cui si vuole modificare le informazioni e che viene poi restituito
     */
    private static void modifyClientInfo(Customer c) {
        boolean modifying = true;
        Customer updatedC = new Customer(c);
        while (modifying) {
            System.out.println("Cosa vuoi modificare?");
            System.out.println("\t 1 - Nome");
            System.out.println("\t 2 - Cognome");
            System.out.println("\t 3 - Indirizzo e-mail");
            System.out.println("\t 4 - Termina modifiche");
            Scanner input = new Scanner(System.in);
            int choice;
            try{
                choice = input.nextInt();
            } catch(InputMismatchException i){
                choice = 0;
            }
            switch (choice) {
                case 1 -> updatedC.set_first_name();
                case 2 -> updatedC.set_last_name();
                case 3 -> {
                    boolean mailIsValid = false;
                    while (!mailIsValid) {
                        try {
                            updatedC.set_email();
                            mailIsValid = true;
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
                case 4 -> {
                    modifying = false;
                    if(CustomerDAO.getINSTANCE().updateInfo(updatedC)) { //Termina le modifiche sul Customer e fa l'update
                        c.copy(updatedC);
                    }
                }
                default -> System.err.println("Opzione non valida...");
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
            System.out.println("\t 6 - Mostra tutti");
            System.out.println("\t 7 - Torna indietro");
            Scanner option = new Scanner(System.in);
            try{
                choice = option.nextInt();
            } catch(InputMismatchException i){
                choice = 0;
            }
            Scanner customerData;
            switch(choice){
                case 1 -> {
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
                    if(name.matches("^([A-ZÀ-Ü^×]){1}([a-zà-ü^÷])*+\\s([A-ZÀ-Ü^×]){1}([a-zà-ü^÷])*+$")) {
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
                    if(name.matches("^([A-ZÀ-Ü^×]){1}([a-zà-ü^÷])*+$")){
                        cd.findByFirstName(name);
                    } else {
                        System.err.println("Formato nome non valido...");
                    }
                }
                case 4 -> {
                    System.out.println("Inserire cognome del cliente: ");
                    customerData = new Scanner(System.in);
                    String surname = customerData.nextLine();
                    if(surname.matches("^([A-ZÀ-Ü^×]){1}([a-zà-ü^÷])*+$")){
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
                case 6 -> cd.findAll();
                case 7 -> search = false;
                default -> System.err.println("Opzione non valida...");
            }
            System.out.println("\n");
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
                        int favoriteType = umbrellaData.nextInt();
                        System.out.println("E' stato selezionato il tipo " + UmbrellaType.getInstance().getUTypeMap().get(favoriteType).getTypeName());
                        ud.findByType(favoriteType); //Se il tipo indicato non è presente in UmbrellaType, tira una nullPointerException; NB non è gestito il caso in cui 0 indica "nessun tipo preferito"
                    } catch(NullPointerException n){
                        System.err.println("Il codice inserito non identifica nessun tipo di ombrellone");
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
    /**
     * Metodo che permette di accedere a un sotto-menù con operazioni che permettono di visualizzare a schermo le prenotazioni
     * verso il bagno secondo criteri selezionabili.
     */

    private static void reservationSearch(){
        boolean rRunning = true;
        ReservationDAO rd = ReservationDAO.getInstance();
        int choice;
        while(rRunning){
            System.out.println("Ricerca per: ");
            System.out.println("\t 1 - ID prenotazione");
            System.out.println("\t 2 - ID cliente");
            System.out.println("\t 3 - Id ombrellone");
            System.out.println("\t 4 - Data");
            System.out.println("\t 5 - Mostra tutte");
            System.out.println("\t 6 - Torna indietro");
            Scanner option = new Scanner(System.in);
            try{
                choice = option.nextInt();
            } catch(InputMismatchException i){
                choice = 0;
            }
            Scanner reservationData;
            switch(choice){
                case 1 -> {
                    System.out.println("Inserisci codice prenotazione: ");
                    reservationData = new Scanner(System.in);
                    try {
                        System.out.println(rd.findById(reservationData.nextInt()));
                    } catch (InputMismatchException i){
                        System.err.println("Inserire un codice numerico...");
                    } catch (SQLException s){
                        System.err.println(s.getMessage());
                    }
                }
                case 2 -> {
                    System.out.println("Inserisci codice cliente: ");
                    reservationData = new Scanner(System.in);
                    try {
                        rd.findByCustomerId(reservationData.nextInt());
                    } catch (InputMismatchException i){
                        System.err.println("Inserire un codice numerico...");
                    } catch(SQLException s ){
                        System.err.println(s.getMessage());
                    }
                }
                case 3 -> {
                    System.out.println("Inserisci codice ombrellone: ");
                    reservationData = new Scanner(System.in);
                    try {
                        rd.findByUmbrellaId(reservationData.nextInt());
                    } catch (InputMismatchException i){
                        System.err.println("Inserire un codice numerico...");
                    } catch (SQLException s){
                        System.err.println(s.getMessage());
                    }
                }
                case 4 -> {
                //    TODO aggiungere eventualmente la possibilita che in assenza di seconda data mostri tutte le prenotazioni dalla data di partenza in poi
                    LocalDate start = LocalDate.now();
                    LocalDate end = LocalDate.now();
                    boolean validStartDate = false;
                    while(!validStartDate){
                        System.out.println("Inserire data di inizio: (dd-mm-yyyy)");
                        try{
                            start = parseDate();
                            validStartDate = true;
                        } catch (NumberFormatException | DateTimeException e){
                            System.err.println(e.getMessage());
                        }
                    }
                    boolean validEndDate = false;
                    while(!validEndDate){
                        System.out.println("Inserire data di fine: (dd-mm-yyyy)");
                        try{
                            end = parseDate();
                            if(end.compareTo(start) >= 0) { // controlla che la data di fine prenotazione sia successiva a quella d'inizio prenotazione
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
                    try{
                        rd.findByDates(start, end);
                    } catch(SQLException e){
                        System.err.println(e.getMessage());
                    }
                }
                case 5 -> {
                    try{
                        rd.findAll();
                    } catch(SQLException s){
                        System.err.println(s.getMessage());
                    }
                }
                case 6 -> rRunning = false;
                default -> System.err.println("Opzione non valida...");
            }
        }
    }

    /**
     * Metodo che prende in ingresso da Command Line una data in formato gg-mm-aa e la riorganizza in un formato accettato
     * da SQL
     * @return data inserita ma in un formato accettato da SQL
     */
    private static LocalDate parseDate(){
        Scanner s = new Scanner(System.in);
        String date = s.nextLine();
        String[] full_date = date.split("-");
        LocalDate _date;
        try{
            int dayOfMonth = Integer.parseInt(full_date[0]);
            int month = Integer.parseInt(full_date[1]);
            int year = Integer.parseInt(full_date[2]);
            _date = LocalDate.of(year, month, dayOfMonth);
        } catch(NumberFormatException n){
            throw new NumberFormatException("Inserire valori numerici...");
        } catch (DateTimeException d){
            throw new DateTimeException("La data " + date + " non è valida...");
        }
        return _date;
    }

    /**
     * Metodo che permette di accedere a un sotto-menù con operazioni che permettono di visualizzare a schermo le ricevute
     * relative alle prenotazioni secondo criteri selezionabili.
     */
    private static void paymentSearch(){
        boolean pRunning = true;
        InvoiceDAO id = InvoiceDAO.getINSTANCE();
        int choice;
        while(pRunning){
            System.out.println("Ricerca per: ");
            System.out.println("\t 1 - Ricerca per codice ricevuta" );
            System.out.println("\t 2 - Ricerca per codice cliente");
            System.out.println("\t 3 - Ricerca per stato pagamento");
            System.out.println("\t 4 - Torna indietro");
            Scanner input = new Scanner(System.in);
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
                        System.out.println(id.findByInvoiceID(input.nextInt()));
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
