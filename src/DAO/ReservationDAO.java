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
     * Metodo che mostra a schermo le prenotazioni che matchano l'ID del cliente inserito
     * @param id: id del cliente relativo alle prenotazioni cercate
     */
    public void findByCustomerId(int id) throws SQLException {
        String query = "select * from reservation where customerid = " + id;
        try{
            showReservations(query);
        } catch (SQLException e) {
            throw new SQLException("Non sono state trovate prenotazioni con i dati forniti");
        }
    }

    /**
     * Metodo che mostra a schermo le prenotazioni che matchano l'ID dell'ombrellone inserito
     * @param id: id dell'ombrellone relativo alle prenotazioni cercate
     */
    public void findByUmbrellaId(int id) throws SQLException {
        String query = "select * from reservation where ombrelloneid = " + id;
        try{
            showReservations(query);
        } catch (SQLException e){
            throw new SQLException("Non sono state trovate prenotazioni con i dati forniti");
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
        try{
            showReservations(query);
        } catch (SQLException e){
            throw new SQLException("Non sono state trovate prenotazioni con i dati forniti");
        }
    }

    /**
     * Metodo che mostra a schermo tutte le prenotazioni registrate sul database
     */
    public void findAll(){
        String query = "select * from reservation";
        try{
            showReservations(query);
        } catch (SQLException e){
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
    private void showReservations(String query) throws SQLException {
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
            throw new SQLException();
        }
        for (Reservation r : rList){
            System.out.println(r);
        }
    }

    /**TODO
     * Le operazioni di modifica passano sempre per una query e un fetch dei dati, però diventa ridondante
     * fare le stesse funzioni per mostrare i dati e per modificare i dati. Si potrebbe invece mettere come secondo parametro
     * delle funzioni di findBy un parametro che indica se deve essere utilizzata per recuperare dati per modificarli o solo
     * per mostrarli. Le funzioni sopra sono per ora accorpate allo standard scritto in InvoiceDAO
     */
}
