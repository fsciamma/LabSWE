package BusinessLogic;

import DAO.CustomerDAO;
import DAO.ReservationDAO;
import DAO.UmbrellaTypeDAO;
import model.*;

import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

public abstract class BusinessLogic {
    /**
     * Metodo che mostra il menù principale del programma, permette di accedere ai metodi per eseguire operazioni su clienti o prenotazioni o chiudere il programma
     */
    public static void mainMenu(){
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

    /**
     * Metodo che mostra un sottomenù con le operazioni eseguibili riguardo ad un cliente
     */
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

    /**
     * Metodo che invoca i metodi di Customer e CustomerDAO per aggiungere un nuovo cliente al database
     */
    private static void addNewCustomer() {
        CustomerDAO cd = CustomerDAO.getINSTANCE();
        Customer newC = Customer.createNewCustomer();
        try {
            cd.addNewCustomer(newC);
        } catch (SQLException e) {
            System.out.println("Un cliente con le stesse credenziali è già salvato in memoria");
        }
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
            CustomerDAO cd = CustomerDAO.getINSTANCE();
            Scanner findC_input = new Scanner(System.in);
            switch (findC_input.nextInt()) {
                case 1 -> {
                    System.out.println("Inserire codice cliente:");
                    Scanner customerNumber = new Scanner(System.in);
                    Customer c = cd.findById(customerNumber.nextInt());
                    if(c.get_first_name() != null) { // non permette di modificare il cliente se non lo trova nel database
                        //TODO aggiungere un nuovo sottomenù dove sono mostrate le opzioni che possono essere scelte: modifica info cliente, crea nuova prenotazione, modifica prenotazione, cancella prenotazione...
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

    private static void addNewReservation(int customerId) throws SQLException {
        ReservationDAO rd = ReservationDAO.getInstance();
        Reservation newRes = Reservation.createNewReservation(customerId);
        //TODO aggiungere un metodo per la ricerca di ombrelloni: permettere di ricercare ombrelloni di un certo tipo e aggiungerlo alla prenotazione appena creata (non era corretto inserirlo in createNewReservation in quanto andiamo a fare una query sugli ombrelloni). Il metodo potrebbe essere una cosa del tipo findUmbrella(Date newRes.getStart_date(), Date newRes.getEnd_date(), int tipoOmbrellone)
        //TODO decidere se inserire le righe di codice che mostrano i tipi di ombrellone in un metodo a parte
        UmbrellaTypeDAO uTD = UmbrellaTypeDAO.getInstance(); //Le righe successive mostrano a schermo il tipo di ombrellone che si può scegliere
        UmbrellaType umbrellaTable = uTD.getUTypes();
        System.out.println("Seleziona il tipo di ombrellone:");
        for(Map.Entry<Integer, TypeDetails> type: umbrellaTable.getUTypeMap().entrySet()){ //cicla sugli elementi di umbrellaTable e li printa a schermo
            System.out.println("\t" + type.getKey() + " - " + type.getValue().getTypeName());
        }
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
