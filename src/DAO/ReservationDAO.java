package DAO;

import model.*;

import java.math.BigDecimal;
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
     * Aggiorna la tabella reservation
     * @param newR La Reservation che deve essere inserita nel database
     * @return id della prenotazione da passare alla tabella reserved_assets
     */
    public int addNewReservation(Reservation newR) throws SQLException{
        String insertStatement = "INSERT INTO \"laZattera\".reservation (\"customerID\") values (?) " +
                "RETURNING \"reservationID\"";
        int id;
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
        int new_id;
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
                " where \"customerID\" = '" + email + "'";
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
                r.compute_total();
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
                isFound = true;
            }
            if(isFound){
                System.out.println(s);
            }
        }
        return isFound;
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

    public void deleteReservedAddOn(ReservedAddOn rao){
        String query = "delete from \"laZattera\".reserved_add_on where reserved_add_on.\"add_onID\" = " + rao.getAddon().getAdd_onId() +" and start_date = '" + rao.getStart_date() + "'";
        try(Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
            System.out.println("Gli AddOn sono stati cancellati correttamente");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void totalDestruction(int resCode) {
        AssetDAO.getINSTANCE().totalDestruction(resCode);
        InvoiceDAO.getINSTANCE().deleteInvoice(resCode);

        String query = "delete from \"laZattera\".reservation where \"reservationID\" = " + resCode;
        try(Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
            System.out.println("La prenotazione è stata cancellata con successo");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePrice(int resCode, BigDecimal offset) {
        String query = "select * from \"laZattera\".invoice where \"reservationID\" = " + resCode + ";";
        try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                BigDecimal newTot = offset.add(rs.getBigDecimal("total"));
                rs.updateBigDecimal("total", newTot);
                rs.updateRow();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ritorna il numero univoco del Reserved Asset dati il numero della prenotazione e l'ID dell'asset
     * @param resCode
     * @param ra
     * @return l'ID del ReservedAsset sul database
     */
    public int findReservedAssetNumber(int resCode, ReservedAsset ra) {
        String query = "select \"reservedID\" from \"laZattera\".reserved_assets where \"reservationID\" = " + resCode + " and \"assetID\" = " + ra.getAsset().getResId();
        int value = 0;
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                value = rs.getInt("reservedID");
            }
        } catch (SQLException s) {
            System.err.println(s.getMessage());
        }
        return value;
    }

    /**
     * @param resCode ID della prenotazione di cui si vuole conoscere gli ID degli asset prenotati
     * @return La lista degli assetID prenotati
     */
    public ArrayList<Integer> getAssetsInReservation(int resCode) {
        String query = "select \"assetID\" from \"laZattera\".reserved_assets where \"reservationID\" = " + resCode;
        ArrayList<Integer> assetList = new ArrayList<>();
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                assetList.add(rs.getInt("assetID"));
            }
        } catch (SQLException s){
            System.err.println(s.getMessage());
        }
        return assetList;
    }

    /**
     * Genera un ArrayList contenente le informazioni sul ReservedAsset associato a resCode e assetCode. È considerato il caso in cui uno stesso Asset sia associato in più periodi a una Reservation, per questo motivo ritorna una lista e non un singolo Asset
     * @param resCode il codice della Reservation di cui si vuole modificare un ReservedAsset
     * @param assetCode il codice del ReservedAsset da modificare
     * @return un ArrayList contenente tutti ReservedAsset associati a resCode e assetCode, ognuno con le proprie date
     */
    public ArrayList<ReservedAsset> findRA(int resCode, int assetCode) {
        String query = "select \"reservedID\", start_date, end_date from \"laZattera\".reserved_assets where \"reservationID\" = " + resCode + " and  \"assetID\" = " + assetCode;
        ArrayList<ReservedAsset> raList = new ArrayList<>();
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()) {
                ReservedAsset ra = new ReservedAsset();
                ra.setAsset(AssetDAO.getINSTANCE().findByID(assetCode));
                ra.setAdd_ons(new ArrayList<>());
                ra.setStart_date(rs.getDate("start_date").toLocalDate());
                ra.setEnd_date(rs.getDate("end_date").toLocalDate());
                raList.add(ra);
            }
        } catch (SQLException s){
            System.err.println(s.getMessage());
        }
        return raList;
    }
}
