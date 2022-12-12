package DAO;

import model.Asset;
import model.Reservation;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ReservationDAO extends BaseDAO {

    private static ReservationDAO INSTANCE;

    private ReservationDAO(){
        super();
    }

    public static ReservationDAO getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ReservationDAO();
        }
        return INSTANCE;
    }

    //ADD METHODS//
    /**
     * Aggiunge una reservation al database aggiornando tutte le tabelle influenzate
     * @param r : reservation, date
     */
    public int updateReservationTables(Reservation r, ArrayList<Integer> reserved_assets) {
        int id = 0;
        try{
            // Aggiornamento tabella reservation
            id = addNewReservation(r);
            for (Integer i: reserved_assets) {
                // Aggiornamento tabella reserved_assets
                updateReservedAssetReservationIDValue(i, id);
                //TODO ciclo for per gli addon di ciascun reservable asset
            }
            return id;
        } catch (SQLException s) {
            for(Integer i: reserved_assets){
                deleteReservedAsset(i);
            }
            if(id > 0)
                deleteReservation(id);
            throw new RuntimeException("Errore nell'aggiornamento delle tabelle; annulamento operazione");
        }
    }

    /**
     * Aggiorna la tabella reservation
     * @param newR La Reservation che deve essere inserita nel database
     * @return id della prenotazione da passare alla tabella reserved_assets
     */
    public int addNewReservation(Reservation newR) throws SQLException{
        String insertStatement = "INSERT INTO \"laZattera\".reservation (\"customerID\") values (?) " +
                "RETURNING \"reservationID\"";
        int id = 0;
        ResultSet rs;
        try(PreparedStatement stmt = conn.prepareStatement(insertStatement)){
            stmt.setString(1, newR.getCustomer());
            stmt.execute();

            rs = stmt.getResultSet();
            rs.next();
            id = rs.getInt("reservationID");

        }
        //TODO inserire un eccezione se id risulta essere ancora 0
        return id;
    }

    /**
     * Aggiorna la tabella resesrved_assets
     * @param r: asset inserito nella prenotazione
     * @param sd: data di inizio della prenotazione
     * @param ed: data di fine della prenotazione
     * @return : id dell'assegnazione
     */
    public int addNewReserved_asset(Asset r, LocalDate sd, LocalDate ed) throws SQLException{
        String insertStatement = "INSERT INTO \"laZattera\".reserved_assets (\"assetID\", start_date, end_date) " +
                "values(?, ?, ?) " +
                "RETURNING \"reservedID\"";
        int new_id = 0;
        ResultSet rs;
        try(PreparedStatement stmt = conn.prepareStatement(insertStatement)){
            stmt.setInt(1, r.getResId());
            stmt.setDate(2, Date.valueOf(sd));
            stmt.setDate(3, Date.valueOf(ed));
            
            stmt.execute();

            rs = stmt.getResultSet();
            rs.next();
            new_id = rs.getInt("reservedID");
        }
        //TODO inserire un eccezione se id risulta essere ancora 0
        return new_id;
    }

    //FIND METHODS//
    /**
     * Metodo che fetcha una prenotazione dal Database con l'id inserito
     * @param id: id della prenotazione da cercare
     * @return Reservation r: istanza della prenotazione presente sul Database
     */
    public Reservation findById(int id) throws SQLException {
        String query = "select * from \"laZattera\".reservation where \"reservationID\" = " + id;
        return getReservation(query);
    }

    /**
     * Metodo che mostra a schermo le prenotazioni che matchano l'email del cliente inserito
     * @param email: email del cliente relativo alle prenotazioni cercate
     */
    public void findByCustomerId(String email) throws SQLException {
        String query = "select * from \"laZattera\".reservation" +
                " where \"customerID\" = '" + email + "'"; //TODO prob andrà fatto un altro join per visualizzare anche gli addOn
        if(!showReservations(query)){
            throw new SQLException("Non sono state trovate prenotazioni per il cliente " + email);
        }
    }

    /**
     * Metodo che mostra a schermo le prenotazioni che matchano l'ID dell'ombrellone inserito
     * @param id: id dell'ombrellone relativo alle prenotazioni cercate
     */
    //TODO da rivedere
    public void findByUmbrellaId(int id) throws SQLException {
        String query = "select * from \"laZattera\".reservation where ombrelloneid = " + id;
        if(!showReservations(query)){
            throw new SQLException("Non sono state trovate prenotazioni per l'ombrellone #" + id);
        }
    }

    /**
     * Metodo che mostra a schermo le prenotazioni attive nell'intervallo di date inserite (comprese). Per prenotazione
     * attiva si intende una prenotazione che inizia, finisce o si svolge tra le due date.
     * @param start: data d'inizio dell'intervallo di ricerca
     * @param end: data di fine dell'intervallo di ricerca
     */
    //TODO da modificare secondo nuovo schema
    public void findByDates(LocalDate start, LocalDate end) throws SQLException {
        String query = "select * from \"laZattera\".reservation where end_date >= '" + start + "' and start_date <= '" + end + "'";
        if(!showReservations(query)){
            throw new SQLException("Non sono state trovate prenotazioni comprese tra " + start + " e " + end);
        }
    }

    /**
     * Metodo che mostra a schermo tutte le prenotazioni registrate sul database
     */
    public void findAll() throws SQLException {
        String query = "select * from \"laZattera\".reservation";
        if(!showReservations(query)){
            System.err.println("Non ci sono prenotazioni attive");
        }
    }

    /**
     * Metodo che fetcha una prenotazione dal database secondo la query immessa.
     * @param query: query per selezionare prenotazioni dal Database
     * @return Reservation: istanza della prenotazione cercata dalla query nel Database
     */
    private Reservation getReservation(String query) throws SQLException {
        Reservation r = new Reservation();
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                r.setReservationId(rs.getInt("reservationID"));
                //TODO manca il corpo
                //r.setTotal_price(rs.getBigDecimal("total_price"));
            }
        }
        if(r.getReservationId() == 0){
            throw new SQLException("La prenotazione non è stata trovata");
        }
        return r;
    }

    /**
     * Metodo usato per mostrare a schermo una o più prenotazioni cercate nel database
     * @param query: query per selezionare una o più prenotazioni dal database
     */
    private boolean showReservations(String query) throws SQLException {
        boolean isFound = false;
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            String s = "";
            while(rs.next()){
                if(!isFound){ //è falso solo la prima volta che esegue rs.next
                    s = "Cliente: " + rs.getString("customerID");
                }
                int resID = rs.getInt("reservationID");
                s = s + "\n * Codice prenotazione: " + resID + "\n" + AssetDAO.showReservedAssets(resID);
                //TODO manca il corpo: id cliente che ha prenotato, i reservable asset prenotati e per ciascuno le date di prenotazione e i reservable addOn prenotati e le loro date di prenotazione, infine il prezzo totale della prenotazione
                //r.setTotal_price(rs.getBigDecimal("total_price"));
                isFound = true;
            }
            if(isFound){
                System.out.println(s);
            }
        }
        return isFound;
    }

    //UPDATE METHODS//
    /**
     * Metodo per aggiornare la tabella reserved_assets al completamento della prenotazione
     * @param reservedCode
     * @param reservationCode
     */
    public void updateReservedAssetReservationIDValue(int reservedCode, int reservationCode) throws SQLException {
        String query = "select * from \"laZattera\".reserved_assets where \"reservedID\" = " + reservedCode;
        try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                rs.updateInt("reservationID", reservationCode);
                rs.updateRow();
            }
        }
    }

    //DELETE METHODS//
    /**
     * Metodo che permette di cancellare una riga dalla tabella reservation
     * @param resCode: identificativo della prenotazione da cancellare
     */
    public void deleteReservation(int resCode) {
        String query = "delete from \"laZattera\".reservation where \"reservationID\" = " + resCode;
        try(Statement stmt = conn.createStatement()){
            stmt.execute(query);
            System.out.println("La prenotazione è stata cancellata!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteReservedAsset(int resCode) {
        String query = "delete from \"laZattera\".reserved_assets where \"reservedID\" = " + resCode;
        try(Statement stmt = conn.createStatement()){
            stmt.executeQuery(query);
            System.out.println("L'asset è stato cancellato correttamente");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
