package DAO;

import model.Reservation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    /**
     * Permette di aggiungere una nuova Reservation al database
     * @param newR La Reservation che deve essere inserita nel database
     */
    public void addNewReservation(Reservation newR){
        String query = "select * from reservation";
        ResultSet rs;
        try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){
            rs = stmt.executeQuery(query);

            rs.moveToInsertRow();

            rs.updateInt("customerid", newR.getCustomerId());
            rs.updateInt("ombrelloneid", newR.getOmbrelloneId());
            rs.updateDate("start_date", newR.getSQLStart_date());
            rs.updateDate("end_date", newR.getSQLEnd_date());
            rs.updateBigDecimal("total_price", newR.getTotal_price());

            rs.insertRow();
            rs.beforeFirst();
        } catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Metodo che fetcha una prenotazione dal Database con l'id inserito
     * @param id: id della prenotazione da cercare
     * @return Reservation r: istanza della prenotazione presente sul Database
     */
    public Reservation findById(int id) throws SQLException {
        String query = "select * from reservation where reservationid = " + id;
        return getReservation(query);
    }

    /**
     * (Nome provvisorio) metodo che fetcha un'unica prenotazione dal Database con id ombrellone e data d'inizio
     * inseriti
     * @param umbrellaId: id dell'ombrellone relativo alla prenotazione ricercata
     * @param start_date: data d'inizio della prenotazione ricercata
     * @return Reservation : istanza della prenotazione con i parametri richiesti presente sul Database
     */
    public Reservation findUnique(int umbrellaId, LocalDate start_date) throws SQLException{
        String query = "select * from reservation where ombrelloneid = " + umbrellaId + " and start_date = '" + start_date + "'";
        return  getReservation(query);
    }

    /**
     * Metodo che mostra a schermo le prenotazioni che matchano l'ID del cliente inserito
     * @param id: id del cliente relativo alle prenotazioni cercate
     */
    public void findByCustomerId(int id) throws SQLException {
        String query = "select * from reservation where customerid = " + id;
        if(!showReservations(query)){
            throw new SQLException("Non sono state trovate prenotazioni per il cliente #" + id);
        }
    }

    /**
     * Metodo che mostra a schermo le prenotazioni che matchano l'ID dell'ombrellone inserito
     * @param id: id dell'ombrellone relativo alle prenotazioni cercate
     */
    public void findByUmbrellaId(int id) throws SQLException {
        String query = "select * from reservation where ombrelloneid = " + id;
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
        String query = "select * from reservation where end_date >= '" + start + "' and start_date <= '" + end + "'";
        if(!showReservations(query)){
            throw new SQLException("Non sono state trovate prenotazioni comprese tra " + start + " e " + end);
        }
    }

    /**
     * Metodo che mostra a schermo tutte le prenotazioni registrate sul database
     */
    public void findAll() throws SQLException {
        String query = "select * from reservation";
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
                r.setReservationId(rs.getInt("reservationid"));
                r.setCustomerId(rs.getInt("customerid"));
                r.setOmbrelloneId(rs.getInt("ombrelloneid"));
                r.setStart_date(rs.getDate("start_date").toLocalDate());
                r.setEnd_date(rs.getDate("end_date").toLocalDate());
                r.setTotal_price(rs.getBigDecimal("total_price"));
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
        boolean isFound = true;
        ArrayList<Reservation> rList = new ArrayList<>();
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                Reservation r = new Reservation();
                r.setReservationId(rs.getInt("reservationid"));
                r.setCustomerId(rs.getInt("customerid"));
                r.setOmbrelloneId(rs.getInt("ombrelloneid"));
                r.setStart_date(rs.getDate("start_date").toLocalDate());
                r.setEnd_date(rs.getDate("end_date").toLocalDate());
                r.setTotal_price(rs.getBigDecimal("total_price"));
                rList.add(r);
            }
        }
        if(rList.isEmpty()){
            isFound = false;
        }
        for (Reservation r : rList){
            System.out.println(r);
        }
        return isFound;
    }

    /**
     * Metodo che permette di cancellare una riga dalla tabella reservation
     * @param resCode: identificativo della prenotazione da cancellare
     */
    public void deleteReservation(int resCode) {
        String query = "delete from reservation where reservationid = " + resCode;
        try(Statement stmt = conn.createStatement()){
            stmt.execute(query);
            System.out.println("La prenotazione è stata cancellata!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
