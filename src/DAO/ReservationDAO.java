package DAO;

import model.*;

import java.sql.*;
import java.time.LocalDate;

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
        return id;
    }

    public int addNewReserved_asset(int reservationID, ReservedAsset a) throws SQLException{
        String insertStatement = "INSERT INTO \"laZattera\".reserved_assets (\"reservationID\", \"assetID\", start_date, end_date) " +
                "values(?, ?, ?, ?) " +
                "RETURNING \"reservedID\"";
        int new_id = 0;
        ResultSet rs;
        try(PreparedStatement stmt = conn.prepareStatement(insertStatement)){
            stmt.setInt(1, reservationID);
            stmt.setInt(2, a.getAsset().getResId());
            stmt.setDate(3, Date.valueOf(a.getStart_date()));
            stmt.setDate(4, Date.valueOf(a.getEnd_date()));
            
            stmt.execute();

            rs = stmt.getResultSet();
            rs.next();
            new_id = rs.getInt("reservedID");
        }
        return new_id;
    }

    public void addNewReservedAddOn(int reservedID, ReservedAddOn a) throws SQLException {
        String insertStatement = "INSERT INTO \"laZattera\".reserved_add_on (\"reserved_assetsID\", \"add_onID\", start_date, end_date) " +
                "values("+ reservedID + ", " + a.getAddon().getAdd_onId() + ", '" + a.getStart_date() + "', '" + a.getEnd_date() +"')";
        try(Statement stmt = conn.createStatement()){
            stmt.executeUpdate(insertStatement);
        }
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
    public void findByAssetId(int id) throws SQLException {
        String query = "select * from \"laZattera\".reservation a join " +
                "\"laZattera\".reserved_assets b on a.\"reservationID\" = b.\"reservationID\"" +
                "where \"assetID\" = " + id + "order by a.\"customerID\"";
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
    public void findByDates(LocalDate start, LocalDate end) throws SQLException {
        String query = "select * from \"laZattera\".reservation a join \"laZattera\".reserved_assets b on a.\"reservationID\"" +
                " = b.\"reservationID\"" +
                " where b.end_date >= '" + start + "' and b.start_date <= '" + end + "'" +
                " order by a.\"customerID\"";
        if(!showReservations(query)){
            throw new SQLException("Non sono state trovate prenotazioni comprese tra " + start + " e " + end);
        }
    }

    /**
     * Metodo che mostra a schermo tutte le prenotazioni registrate sul database
     */
    public void findAll() throws SQLException {
        String query = "select * from \"laZattera\".reservation a join \"laZattera\".reserved_assets b on a.\"reservationID\" = " +
                "b.\"reservationID\" order by a.\"customerID\"";
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
                r.setCustomer(rs.getString("customerID"));
                r.setReserved_Assets(AssetDAO.getINSTANCE().getReservedAssets(rs.getInt("reservationID")));
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
            String last = "";
            while(rs.next()){
                String customer = rs.getString("customerID");
                if(!last.equals(customer)){
                    s = s + "Cliente: " + customer;
                    last = customer;
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
            stmt.executeUpdate(query);
            System.out.println("La prenotazione è stata cancellata!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteReservedAsset(int resCode) {
        String query = "delete from \"laZattera\".reserved_assets where \"reservedID\" = " + resCode;
        try(Statement stmt = conn.createStatement()){
            stmt.executeUpdate(query);
            System.out.println("L'asset è stato cancellato correttamente");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteReservedAddOn(int reservedID) {
        String query = "delete from \"laZattera\".reserved_add_on where \"reserved_assetsID\" = " + reservedID;
        try(Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
            System.out.println("Gli AddOn sono stati cancellati correttamente");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void totalDestruction(int resCode) {
        AssetDAO.getINSTANCE().totalDestruction(resCode);

        String query = "delete from \"laZattera\".reservation where \"reservationID\" = " + resCode;
        try(Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
            System.out.println("La prenotazione è stata cancellata con successo");
            //TODO aggiungere delete della Invoice
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
