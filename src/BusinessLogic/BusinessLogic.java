package BusinessLogic;

import DAO.*;
import model.*;

import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Objects;
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
                case 2 -> System.out.println("-- PRENOTAZIONE --"); //TODO
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
        boolean running = true;
        while(running) {
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
                    running = false;
                }
                default -> System.err.println("Opzione non valida...");
            }
        }
    }

    /**
     * Metodo che invoca i metodi di Customer e CustomerDAO per aggiungere un nuovo cliente al database
     */
    private static void addNewCustomer(){
        CustomerDAO cd = CustomerDAO.getINSTANCE(); //TODO qui andrebbe la factory
        Customer newC = Customer.createNewCustomer();
        try {
            cd.addNewCustomer(newC);
            System.out.println("Vuoi effettuare una prenotazione? (Y/N)");
            Scanner input = new Scanner(System.in);
            String line = input.nextLine();
            if ("Y".equals(line) || "y".equals(line)) { // Se viene inserito qualsiasi altro carattere esce dall'if
                addNewReservation(newC.get_email());
            } else {
                System.out.println("Non è stata aggiunta nessuna prenotazione.");
            }
        } catch (SQLException s){
            System.err.println(s.getMessage());
        }
    }

    /**
     * Metodo che permette di scegliere come eseguire la ricerca di un cliente nel database; se il cliente è presente nel database, invoca anche il metodo che permette di modificarne i campi
     */
    private static void findCustomer() {
        CustomerDAO cd = CustomerDAO.getINSTANCE();
        String email = "";
        boolean emailNotValid = true;
        while(emailNotValid){
            System.out.println("Inserire e-mail cliente:");
            email = new Scanner(System.in).nextLine();
            if(email.matches("^(.+)@(.+).(.+)$")){
                emailNotValid = false;
            }
            else{
                System.err.println("Indirizzo e-mail non valido...");
            }
        }
        try{
            Customer c = cd.findByEMail(email);
            customerMenu(c);
        } catch (SQLException s) {
            System.err.println(s.getMessage());
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
                case 1 -> addNewReservation(c.get_email());
                case 2 -> showReservations(c.get_email());
                case 3 -> modifyCustomerInfo(c);
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
     * @param email L'identificativo del Customer di cui si vogliono vedere le Reservation effettuate
     */
    private static void showReservations(String email){
        try {
            ReservationDAO.getInstance().findByCustomerId(email);
            reservationOptionMenu(email);
        } catch (SQLException s){
            System.err.println(s.getMessage());
        }
    }

    /**
     * Metodo che permette di scegliere quale operazione compiere su una delle Reservations legate al Customer
     * @param email Identificativo del Customer di cui sono state cercate le prenotazioni: viene usato per controllare
     *              che non venga modificata o cancellata una prenotazione che non gli appartiene
     */
    private static void reservationOptionMenu(String email) { //TODO da rivedere insieme al metodo di modifica della prenotazione
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
                case 1 -> modifyReservation(email);
                case 2 -> deleteReservation(email);
                case 3 -> {
                    System.out.println("Torna a pagina precedente");
                    running = false;
                }
                default -> System.err.println("Opzione non valida...");
            }
        }
    }

    private static void modifyReservation(String email) {//TODO

    }

    /**
     * Permette di cancellare una Reservation attraverso l'invocazione del metodo ReservationDAO.deleteReservation se il periodo di annullamento di questa non è scaduto
     * @param email Viene usato per impedire che venga cancellata una prenotazione non appartenente al Customer che identifica
     */
    private static void deleteReservation(String email) {
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
            if(Objects.equals(res.getCustomer(), email) /*&& DAYS.between(LocalDate.now(), res.getStart_date()) >= 7*/) { //TODO da trovare un modo per recuperare la data
                InvoiceDAO.getINSTANCE().deleteInvoice(resCode);
                ReservationDAO.getInstance().deleteReservation(resCode);
            } else {
                System.out.println("Non puoi cancellare questa prenotazione! Il periodo per annullare la prenotazione è scaduto!");
            }
        } catch (SQLException s){
            System.err.println(s.getMessage());
        }
    }

    /**
     * Metodo che permette di aggiungere una Reservation a nome di un cliente appena aggiunto al database oppure che è
     * stato appena cercato
     * @param customerEmail Email del cliente che richiede una nuova prenotazione
     */
    private static void addNewReservation(String customerEmail){
        try{
            //TODO rivedere un po' tutte le eccezioni in questo pezzo di codice
            Reservation newRes = Reservation.createNewReservation(customerEmail);

            //TODO valutare se trasferirlo in una funzione a parte
            ArrayList<Integer> added = new ArrayList<>();
            ArrayList<AddOn> a = new ArrayList<>();
            ReservationDAO rd = ReservationDAO.getInstance();
            int reservedID = 0;
            boolean selecting = true;

            do {
                // Inizio chiedendo al cliente di inserire le date
                LocalDate start_date = setStart_date();
                LocalDate end_date = setEnd_date(start_date);

                // Faccio scegliere al cliente il reservable asset che si vuole prenotare e lo aggiungo alla prenotazione
                // inoltre aggiungo il reserved asset alla lista, tenendo traccia di tutti i reserved aggiunti
                try {
                    Asset the_chosen_one = chooseReservableAsset(start_date, end_date);
                    reservedID = rd.addNewReserved_asset(the_chosen_one, start_date, end_date);
                    added.add(reservedID);

                    // funzione per la selezione degli add on per ciascun asset
                    System.out.println("Vuoi aggiungere AddOn a questo asset? (Y/N)");
                    Scanner input = new Scanner(System.in);
                    String line = input.nextLine();
                    if ("Y".equals(line) || "y".equals(line)) { // Se viene inserito qualsiasi altro carattere esce dall'if
                        a.addAll(reserveAddOns(reservedID, start_date, end_date, newRes));
                        the_chosen_one.setAddOns(a);
                    }

                    // Calcolo del prezzo
                    int d = ((int) DAYS.between(start_date, end_date)) + 1;
                    newRes.updateReservation(the_chosen_one, d);

                } catch (SQLException s) {
                    //Rollback: siccome ho già inserito su DB i reservable asset scelti e rispettivi add on,
                    //se la procedura fallisce devo cancellare tutto ciò che ho aggiunto fino ad ora.
                    System.err.println(s.getMessage());
                    if(!a.isEmpty()){
                        for(Integer i: added){
                            rd.deleteReservedAddOn(i);
                        }
                    }
                    for(Integer i: added){
                        rd.deleteReservedAsset(i);
                    }
                    throw new RuntimeException("Errore nell'aggiunta degli asset; annullamento operazione");
                }

                System.out.println("Vuoi aggiungere altro? (Y/N)");
                Scanner input = new Scanner(System.in);
                String line = input.nextLine();
                if ("N".equals(line) || "n".equals(line)) { // Se viene inserito qualsiasi altro carattere esce dall'if
                    selecting = false;
                }
            } while(selecting);

            // Aggiungo la reservation al DB per poter generare l'ID e creare l'Invoice
            int id = rd.updateReservationTables(newRes, added);

            try{
                addNewInvoice(newRes, id);
            } catch (RuntimeException r){
                //Rollback: devo cancellare tutto a partire dagli add on fino all'invoice
                System.err.println(r.getMessage());
                if(!a.isEmpty()){
                    for(Integer i: added){
                        rd.deleteReservedAddOn(i);
                    }
                }
                // Cancello tutti i reservable asset associati
                for(Integer i: added){
                    rd.deleteReservedAsset(i);
                }
                // Cancello la prenotazione effettuata
                rd.deleteReservation(id);
                throw new RuntimeException("Errore nell'aggiunta della ricevuta; annullamento operazione");
            }
            System.out.println("Prenotazione effettuata!");
        } catch(RuntimeException r){
            System.err.println(r.getMessage());
        }
    }

    private static Asset chooseReservableAsset(LocalDate start_date, LocalDate end_date) throws SQLException {
        AssetDAO rad = AssetDAO.getINSTANCE();
        System.out.println("Seleziona il tipo di Prenotabile preferito: ");
        int chosen_type = chooseType();
        ArrayList<Integer> av = rad.checkAvailability(start_date, end_date, chosen_type);
        int number =  chooseNumber(av);
        return rad.findByID(number);
    }


    private static int chooseType() {
        int fav_type;
        try{
            System.out.println("Inserire il numero del tipo selezionato: ");
            AssetDAO rad = AssetDAO.getINSTANCE();
            System.out.println("\t0 - Nessuna preferenza");
            rad.showTypeTable();
            fav_type = new Scanner(System.in).nextInt();
            System.out.println("E' stato richiesto un ombrellone del tipo: " + rad.fecthType(fav_type));
        } catch (SQLException s){
            System.err.println("Errore nella lettura della tabella");
            fav_type = 0; //TODO
        } catch (InputMismatchException i){
            System.out.println("Nessun tipo specifico richiesto");
            fav_type = 0;
        }
        return fav_type;
    }

    private static int chooseNumber(ArrayList<Integer> av) {
        boolean valid_number = false;
        int choice = 0;
        while(!valid_number){
            try{
                System.out.println("Inserire il numero dell'asset selezionato: ");
                choice = new Scanner(System.in).nextInt();
                if(av.contains(choice)){
                    valid_number = true;
                } else {
                    System.out.println("Asset selezionato non disponibile");
                }
            } catch (InputMismatchException i){
                System.err.println("Seleziona uno degli asset disponibili");
            }
        }
        return choice;
    }

    private static ArrayList<AddOn> reserveAddOns(int reservedID, LocalDate start_date, LocalDate end_date, Reservation r) throws SQLException {
        ArrayList<AddOn> addOnList = new ArrayList<>();
        ReservationDAO rd = ReservationDAO.getInstance();
        boolean selecting = true;

        while(selecting){
            // Selezione date per l'addOn
            LocalDate addOn_sd;
            LocalDate addOn_ed;
            System.out.println("Vuole prenotare l'asset per tutta la durata della prenotazione? (Y/N)");
            Scanner input = new Scanner(System.in);
            String line = input.nextLine();
            if ("N".equals(line) || "n".equals(line)) {
                // Personalizzo le date se non si vuole l'extra per tutta la durata
                addOn_sd = set_addOn_start_date(start_date, end_date);
                addOn_ed = set_addOn_end_date(addOn_sd, end_date);
            }
            else{
                // altrimenti lascio le stesse della prenotazione
                addOn_sd = start_date;
                addOn_ed = end_date;
            }
            try{
                // Scelgo l'addon
                AddOn chosen = chooseAddOn(addOn_sd, addOn_ed);
                // Lo aggiungo ai prenotati e a una lista per completare l'Asset corrispondente
                rd.addNewReservedAddOn(chosen, reservedID, addOn_sd, addOn_ed);
                addOnList.add(chosen);
                // Calcolo il prezzo da aggiungere alla prenotazione
                int d = ((int) DAYS.between(start_date, end_date)) + 1;
                r.updatePrice(chosen, d);
            } catch (SQLException s){
                //Rollback, cancello gli addon inseriti
                System.err.println(s.getMessage());
                rd.deleteReservedAddOn(reservedID);
                throw new SQLException("Errore nell'aggiunta degli Add On, annullamento operazione");
            }

            // Procedo all'aggiunta di un
            System.out.println("Vuoi aggiungere altri AddOn a questo asset? (Y/N)");
            String choice = new Scanner(System.in).nextLine();
            if ("N".equals(choice) || "n".equals(choice)) { // Se viene inserito qualsiasi altro carattere esce dall'if
                selecting = false;
            }
        }

        return addOnList;
    }

    private static AddOn chooseAddOn(LocalDate start_date, LocalDate end_date) throws SQLException {
        AddOnDAO aoDAO = AddOnDAO.getINSTANCE();
        System.out.println("Seleziona il tipo di AddOn desiderato: ");
        int chosen_type = chooseAddOnType();
        ArrayList<Integer> av = aoDAO.checkAvailability(start_date, end_date, chosen_type);
        int number =  chooseNumber(av);
        return aoDAO.findByID(number);
    }

    private static int chooseAddOnType() {
        int fav_type;
        try{
            System.out.println("Inserire il numero del tipo selezionato: ");
            AddOnDAO aoDAO = AddOnDAO.getINSTANCE();
            System.out.println("\t0 - Nessuna preferenza");
            aoDAO.showTypeTable();
            fav_type = new Scanner(System.in).nextInt();
            System.out.println("E' stato richiesto un ombrellone del tipo: " + aoDAO.fecthType(fav_type));
        } catch (SQLException s){
            System.err.println("Errore nella lettura della tabella");
            fav_type = 0;
        } catch (InputMismatchException i){
            System.out.println("Nessun tipo specifico richiesto");
            fav_type = 0;
        }
        return fav_type;
    }

    private static LocalDate set_addOn_start_date(LocalDate asset_start_date, LocalDate asset_end_date) {
        boolean validStartDate = false;
        LocalDate start_date = asset_start_date;
        while(!validStartDate) {
            System.out.println("Inserire data di inizio: (dd-mm-yyyy)");
            try {
                LocalDate tmp = set_date();
                if (!tmp.isBefore(asset_start_date) && !tmp.isAfter(asset_end_date)) {
                    start_date = tmp;
                    validStartDate = true;
                } else {
                    System.err.println("La data inserita è esterna all'intervallo di date dell'asset da prenotare");
                }
            } catch (NumberFormatException | DateTimeException | ArrayIndexOutOfBoundsException e){
                System.err.println(e.getMessage());
            }
        }
        return start_date;
    }

    private static LocalDate set_addOn_end_date(LocalDate addOn_start_date, LocalDate asset_end_date){
        boolean validEndDate = false;
        LocalDate end_date = LocalDate.now();
        while(!validEndDate) {
            System.out.println("Inserire data di fine: (dd-mm-yyyy)");
            try {
                LocalDate tmp = set_date();
                if (!tmp.isBefore(addOn_start_date) && !tmp.isAfter(asset_end_date)) {
                    end_date = tmp;
                    validEndDate = true;
                } else {
                    System.err.println("La data inserita è precedente alla data di inizio prenotazione o al di fuori" +
                            " dell'intervallo di date dell'asset da prenotare");
                }
            } catch (NumberFormatException | DateTimeException | ArrayIndexOutOfBoundsException e) {
                System.err.println(e.getMessage());
            }
        }
        return end_date;
    }

    /**
     * Metodo usato per settare una data d'inizio per la prenotazione, modifica una data in formato gg-mm-aa presa in input
     * da Scanner in un formato utilizzabile per SQL
     */
    private static LocalDate setStart_date(){
        boolean validStartDate = false;
        LocalDate start_date = LocalDate.now();
        while(!validStartDate) {
            System.out.println("Inserire data di inizio: (dd-mm-yyyy)");
            try {
                LocalDate tmp = set_date();
                if (tmp.compareTo(LocalDate.now()) >= 0) {
                    start_date = tmp;
                    validStartDate = true;
                } else {
                    System.err.println("La data inserita è precedente alla giornata odierna...");
                }
            } catch (NumberFormatException | DateTimeException | ArrayIndexOutOfBoundsException e){
                System.err.println(e.getMessage());
            }
        }
        return start_date;
    }

    /**
     * Metodo usato per settare una data di fine per la prenotazione, modifica una data in formato gg-mm-aa presa in input
     * da Scanner in un formato utilizzabile per SQL
     */
    private static LocalDate setEnd_date(LocalDate start_date){
        boolean validEndDate = false;
        LocalDate end_date = LocalDate.now();
        while(!validEndDate) {
            System.out.println("Inserire data di fine: (dd-mm-yyyy)");
            try {
                LocalDate tmp = set_date();
                if (tmp.compareTo(start_date) >= 0) {
                    end_date = tmp;
                    validEndDate = true;
                } else {
                    System.err.println("La data inserita è precedente alla data di inizio prenotazione...");
                }
            } catch (NumberFormatException | DateTimeException | ArrayIndexOutOfBoundsException e) {
                System.err.println(e.getMessage());
            }
        }
        return end_date;
    }

    /**
     * Metodo per cambiare il formato di una data da gg-mm-aa a aa-mm-gg
     * @return LocalDate: data nel formato corretto
     */
    private static LocalDate set_date() {
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

    /**
     * Metodo che permette di aggiungere una Invoice relativa a una prenotazione
     * @param res: Prenotazione di cui voglio creare una ricevuta
     */
    private static void addNewInvoice(Reservation res, int id) throws RuntimeException{
        Invoice newInv = new Invoice(id, res.getTotal_price()); //TODO wrapper del costruttore?
        InvoiceDAO iDAO = InvoiceDAO.getINSTANCE();
        iDAO.addNewInvoice(newInv);
    }

    /**
     * Metodo che permette di modificare i campi di un oggetto Customer c che è stato trovato nel database attraverso i metodi CustomerDAO.findById() o CustomerDAO.findByInfo()
     * @param c è il Customer di cui si vuole modificare le informazioni e che viene poi restituito
     */
    private static void modifyCustomerInfo(Customer c) {
        boolean running = true;
        Customer updatedC = new Customer(c);
        while (running) {
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
                case 3 -> { //TODO wrappare?
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
                    running = false;
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
        boolean running = true;
        while(running){
            System.out.println("Selezionare l'operazione che si vuole compiere: ");
            System.out.println("\t 1 - Ricerca per clienti");
            System.out.println("\t 2 - Ricerca per prenotazioni attive");
            System.out.println("\t 3 - Ricerca per pagamenti");
            System.out.println("\t 4 - Torna indietro");
            Scanner input = new Scanner(System.in);
            int choice;
            try{
                choice = input.nextInt();
            } catch(InputMismatchException i){
                choice = 0;
            }
            switch(choice) {
                case 1 -> customerSearch();
                case 2 -> reservationSearch();
                case 3 -> invoiceSearch();
                case 4 -> running = false;
                default -> System.err.println("Opzione non valida...");
            }
        }
    }

    /**
     * Metodo che permette da accedere a un sotto-menù con operazioni che permettono di visualizzare a schermo i clienti
     * registrati al bagno secondo criteri selezionabili.
     */
    private static void customerSearch(){
        boolean search = true;
        int choice;
        while(search){
            System.out.println("Ricerca per: ");
            System.out.println("\t 1 - Email");
            System.out.println("\t 2 - Nome e Cognome");
            System.out.println("\t 3 - Nome");
            System.out.println("\t 4 - Cognome");
            System.out.println("\t 5 - Mostra tutti");
            System.out.println("\t 6 - Torna indietro");
            Scanner option = new Scanner(System.in);
            try{
                choice = option.nextInt();
            } catch(InputMismatchException i){
                choice = 0;
            }
            switch(choice){
                case 1 -> BL_findCustomerByEmail();
                case 2 -> BL_findCustomersByFullName();
                case 3 -> BL_findCustomersByFirstName();
                case 4 -> BL_findCustomersByLastName();
                case 5 -> CustomerDAO.getINSTANCE().findAll();
                case 6 -> search = false;
                default -> System.err.println("Opzione non valida...");
            }
            System.out.println("\n");
        }
    }

    /**
     * Prende in ingresso da tastiera l'indirizzo email del cliente ricercato e passa la String al metodo di CustomerDAO per la ricerca del Customer e printa a schermo le informazioni ritornate
     */
    private static void BL_findCustomerByEmail() {
        boolean emailNotValid = true;
        String email;
        while(emailNotValid){
            System.out.println("Inserire email cliente:");
            email = new Scanner(System.in).nextLine();
            if(email.matches("^(.+)@(.+).(.+)$")){
                emailNotValid = false;
            } else {
                System.err.println("Indirizzo e-mail non valido...");
            }
            try{
                System.out.println(CustomerDAO.getINSTANCE().findByEMail(email));
            } catch(InputMismatchException i){
                System.err.println("Inserire una email cliente...");
            } catch(SQLException s){
                System.err.println(s.getMessage());
            }
        }
    }

    /**
     * Prende in ingresso da tastiera il nome completo del cliente ricercato e passa la String al metodo di CustomerDAO per la ricerca dei Customers corrispondenti e printa a schermo le informazioni ritornate (può ritornare più di un risultato)
     */
    private static void BL_findCustomersByFullName() {
        System.out.println("Inserire nome e cognome del cliente da cercare (formato: Nome Cognome):");
        String name = new Scanner(System.in).nextLine();
        if(name.matches("^([A-ZÀ-Ü^×]){1}([a-zà-ü^÷])*+\\s([A-ZÀ-Ü^×]){1}([a-zà-ü^÷])*+$")) {
            String[] fullName = name.split(" ");
            CustomerDAO.getINSTANCE().findByFullName(fullName);
        } else {
            System.err.println("Formato nome non valido...");
        }
    }

    /**
     * Prende in ingresso da tastiera il nome del cliente ricercato e passa la String al metodo di CustomerDAO per la ricerca dei Customers corrispondenti e printa a schermo le informazioni ritornate (può ritornare più di un risultato)
     */
    private static void BL_findCustomersByFirstName() {
        System.out.println("Inserire nome del cliente: ");
        String name = new Scanner(System.in).nextLine();
        if(name.matches("^([A-ZÀ-Ü^×]){1}([a-zà-ü^÷])*+$")){
            CustomerDAO.getINSTANCE().findByFirstName(name);
        } else {
            System.err.println("Formato nome non valido...");
        }
    }

    /**
     * Prende in ingresso da tastiera il cognome del cliente ricercato e passa la String al metodo di CustomerDAO per la ricerca dei Customers corrispondenti e printa a schermo le informazioni ritornate (può ritornare più di un risultato)
     */
    private static void BL_findCustomersByLastName() {
        System.out.println("Inserire cognome del cliente:");
        String surname = new Scanner(System.in).nextLine();
        if(surname.matches("^([A-ZÀ-Ü^×]){1}([a-zà-ü^÷])*+$")){
            CustomerDAO.getINSTANCE().findByLastName(surname);
        } else {
            System.err.println("Formato cognome non valido...");
        }
    }

    /**
     * Metodo che permette di accedere a un sotto-menù con operazioni che permettono di visualizzare a schermo le prenotazioni
     * verso il bagno secondo criteri selezionabili.
     */

    private static void reservationSearch(){
        boolean running = true;
        int choice;
        while(running){
            System.out.println("Ricerca per: ");
            System.out.println("\t 1 - ID prenotazione");
            System.out.println("\t 2 - Email cliente");
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
            switch(choice){
                case 1 -> BL_findReservationByID();
                case 2 -> BL_findReservationsByCustomer();
                case 3 -> BL_findReservationsByAsset();
                case 4 -> BL_findReservationsByDates();
                case 5 -> {
                    try{
                        ReservationDAO.getInstance().findAll();
                    } catch(SQLException s){
                        System.err.println(s.getMessage());
                    }
                }
                case 6 -> running = false;
                default -> System.err.println("Opzione non valida...");
            }
        }
    }

    /**
     * Prende in ingresso da tastiera il codice della Reservation ricercata e passa il valore al metodo di ReservationDAO per la ricerca della Reservation e printa a schermo le informazioni ritornate
     */
    private static void BL_findReservationByID() {
        System.out.println("Inserisci codice prenotazione: ");
        try {
            System.out.println(ReservationDAO.getInstance().findById(new Scanner(System.in).nextInt()));
        } catch (InputMismatchException i){
            System.err.println("Inserire un codice numerico...");
        } catch (SQLException s){
            System.err.println(s.getMessage());
        }
    }

    /**
     * Prende in ingresso da tastiera l'indirizzo email del cliente ricercato e passa la String al metodo di ReservationDAO per la ricerca delle Reservations associate al cliente e printa a schermo le informazioni ritornate (può ritornare più di un risultato)
     */
    private static void BL_findReservationsByCustomer() {
        System.out.println("Inserisci email cliente:");
        try {
            ReservationDAO.getInstance().findByCustomerId(new Scanner(System.in).nextLine()); //TODO va messo un controllo su come è scritto l'indirizzo email
        } catch (InputMismatchException i){
            System.err.println("Inserire un codice numerico...");
        } catch(SQLException s ){
            System.err.println(s.getMessage());
        }
    }

    /**
     * Prende in ingresso da tastiera il codice dell'Asset ricercato e passa il valore al metodo di ReservationDAO per la ricerca delle Reservations e printa a schermo le informazioni ritornate (può ritornare più di un risultato)
     */
    private static void BL_findReservationsByAsset() {
        System.out.println("Inserisci codice asset: ");
        try {
            ReservationDAO.getInstance().findByUmbrellaId(new Scanner(System.in).nextInt());
        } catch (InputMismatchException i){
            System.err.println("Inserire un codice numerico...");
        } catch (SQLException s){
            System.err.println(s.getMessage());
        }
    }

    /**
     * Prende in ingresso da tastiera le due date del periodo di cui si vogliono conoscere le Reservations  passa i valori al metodo di ReservationDAO per la ricerca delle Reservations e printa a schermo le informazioni ritornate (può ritornare più di un risultato)
     */
    private static void BL_findReservationsByDates() {
        //TODO aggiungere eventualmente la possibilita che in assenza di seconda data mostri tutte le prenotazioni dalla data di partenza in poi
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
            ReservationDAO.getInstance().findByDates(start, end);
        } catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Metodo che prende in ingresso da Command Line una data in formato gg-mm-aa e la riorganizza in un formato accettato da SQL
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
    private static void invoiceSearch(){
        boolean running = true;
        int choice;
        while(running){
            System.out.println("Ricerca per: ");
            System.out.println("\t 1 - Ricerca per codice ricevuta" );
            System.out.println("\t 2 - Ricerca per codice cliente");
            System.out.println("\t 3 - Ricerca per stato pagamento");
            System.out.println("\t 4 - Torna indietro");
            try{
                choice = new Scanner(System.in).nextInt();
            } catch(InputMismatchException i){
                choice = 0;
            }
            switch(choice) {
                case 1 -> BL_findInvoiceByCode();
                case 2 -> BL_findInvoiceByCustomer();
                case 3 -> BL_findInvoiceByState();
                case 4 -> running = false;
                default -> System.err.println("Opzione non valida...");
            }
        }
    }

    /**
     * Prende in ingresso da tastiera il codice della Invoice ricercata e passa il valore al metodo di InvoiceDAO per la ricerca della Invoice e printa a schermo le informazioni ritornate
     */
    private static void BL_findInvoiceByCode() {
        System.out.println("Inserire codice ricevuta/prenotazione:");
        try{
            System.out.println(InvoiceDAO.getINSTANCE().findByInvoiceID(new Scanner(System.in).nextInt()));
        } catch (InputMismatchException i){
            System.err.println("Inserire un codice numerico...");
        } catch(SQLException s){
            System.err.println(s.getMessage());
        }
    }

    /**
     * Prende in ingresso da tastiera l'email del Customer ricercato e passa il valore al metodo di InvoiceDAO per la ricerca della Invoice e printa a schermo le informazioni ritornate (può ritornare più di un risultato)
     */
    private static void BL_findInvoiceByCustomer() {
        System.out.println("Inserire email cliente: ");
        try{
            InvoiceDAO.getINSTANCE().findByCustomerID(new Scanner(System.in).nextLine());
        } catch (InputMismatchException i){
            System.err.println("Inserire un codice numerico...");
        } catch(SQLException s){
            System.err.println(s.getMessage());
        }
    }

    /**
     * Prende in ingresso da tastiera lo stato ricercato del pagamento (effettuato o non effettuato) ricercato e passa il valore al metodo di InvoiceDAO per la ricerca della Invoice e printa a schermo le informazioni ritornate (può ritornare più di un risultato)
     */
    private static void BL_findInvoiceByState() {
        System.out.println("Inserire stato pagamento (true o false): ");
        try{
            InvoiceDAO.getINSTANCE().findByPaymentStatus(new Scanner(System.in).nextBoolean());
        } catch (InputMismatchException i){
            System.err.println("Inserire un valore valido...");
        } catch(SQLException s){
            System.err.println(s.getMessage());
        }
    }
}
