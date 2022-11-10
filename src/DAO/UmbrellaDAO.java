package DAO;

import model.Umbrella;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class UmbrellaDAO extends BaseDAO{
    private static UmbrellaDAO INSTANCE;

    private UmbrellaDAO() {
        super();
    }

    public static UmbrellaDAO getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new UmbrellaDAO();
        }
        return INSTANCE;
    }

    /**
     * Metodo che fetcha un ombrellone dal database secondo la query immessa.
     * @param query: query per la ricerca dell'ombrellone
     * @return Umbrella: Istanza dell'ombrellone cercato dalla query nel Database
     */
    public static Umbrella getUmbrella(String query) throws SQLException {
        Umbrella u = new Umbrella();
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                u.setUmbrellaId(rs.getInt("ombrelloneid"));
                u.setValues(rs.getInt("tipo_ombrellone"));
            }
        }
        if(u.getUmbrellaId() == 0){
            throw new SQLException("L'ombrellone non è stato trovato");
        }
        return u;
    }

    /**
     * Metodo che fetcha un ombrellone dal Database con l'id inserito.
     * @param id: id dell'ombrellone da cercare.
     * @return Umbrella: istanza dell'ombrellone cercato sul Database
     */
    public Umbrella findById(int id) throws SQLException{
        String query = "select * from ombrellone where ombrelloneid = " + id;
        return getUmbrella(query);
    }

    /**
     * Metodo che fetcha un ombrellone dal Database con il tipo inserito.
     * @param typeId: numero che codifica una tipologia di ombrellone da cercare
     */
    public void findByType(int typeId) throws SQLException {
        String query = "select * from ombrellone where tipo_ombrellone = " + typeId;
        showUmbrella(query);
    }

    private void showUmbrella(String query) throws SQLException {
        ArrayList<Umbrella> uList = new ArrayList<>();
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                Umbrella u = new Umbrella();
                u.setUmbrellaId(rs.getInt("ombrelloneid"));
                u.setValues(rs.getInt("tipo_ombrellone"));
                uList.add(u);
            }
        }
        if(uList.isEmpty()){
            throw new SQLException("Non sono stati trovati ombrelloni del tipo selezionato");
        }
        for(Umbrella u: uList){
            System.out.println(u);
        }
    }

    /**
     * Metodo che ritorna gli ombrelloni disponibili presi da una vista creata da showAvailableUmbrellas(), filtrati
     * tramite input per tipo (opzionale).
     * @param type: tipo di ombrellone da filtrare, se si vogliono vedere tutti gli ombrelloni disponibili questo deve
     *            essere = 0
     * @return ArrayList</Integer>: Lista di interi che rappresentano gli ombrelloni disponibili
     */
    public ArrayList<Integer> getAvailableUmbrellas(int type){
        ArrayList<Integer> availableUmbrellas;

        String query = "select *" +
                " from ombrellone join availableUmbrellas on ombrellone.ombrelloneid = availableUmbrellas.ombrelloneid";
        if(type != 0){
            query = query + " where ombrellone.tipo_ombrellone = " + type;
        }
        query = query + " order by ombrellone.ombrelloneid asc";
        availableUmbrellas = printAvailableUmbrellas(query);
        return availableUmbrellas;
    }

    /**
     * Metodo che mostra a schermo gli ombrelloni disponibili selezionati dalla query in ingresso
     * @param query: query per selezionare gli ombrelloni da stampare
     * @return ArrayList</Integer> : lista con gli interi che rappresentano gli ombrelloni appena stampati
     */
    private ArrayList<Integer> printAvailableUmbrellas(String query){
        ArrayList<Integer> availableUmbrellas = new ArrayList<>();
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                availableUmbrellas.add(rs.getInt("ombrelloneid"));
                System.out.println(rs.getInt("ombrelloneid"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return availableUmbrellas;
    }

    /**
     * Si interfaccia col database, fornendo informazioni sulle disponibilità singole dei tipi di ombrelloni
     * @param req_start_date La LocalDate d'inizio prenotazione
     * @param req_end_date La LocalDate di fine prenotazione
     * @return Ritorna TRUE se c'è almeno un ombrellone di qualsiasi tipo disponibile
     */
    public boolean showAvailableUmbrellas(LocalDate req_start_date, LocalDate req_end_date) {
        String queryView = "create or replace view availableUmbrellas as" +
                " select ombrelloneid" +
                " from ombrellone" +
                " except select ombrelloneid" +
                " from reservation" +
                " where start_date <= '" + req_end_date + "' and end_date >= '" + req_start_date + "'";

        String queryTable = "select count(availableUmbrellas.ombrelloneid), tipoombrellone.type_name" +
                " from availableUmbrellas" +
                " right join ombrellone on availableUmbrellas.ombrelloneid = ombrellone.ombrelloneid" +
                " join tipoombrellone on tipoombrellone.typeid = ombrellone.tipo_ombrellone" +
                " group by tipoombrellone.typeid" +
                " order by tipoombrellone.typeid";

        try(Statement stmt = conn.createStatement()) {
            stmt.execute(queryView); //crea una view contenente gli ombrelloni disponibili nelle date selezionate

            if(!stmt.executeQuery("select * from availableUmbrellas").next()){ //controlla se la view è vuota
                return false;
            }

            ResultSet rs = stmt.executeQuery(queryTable);
            System.out.println("Seleziona il tipo di ombrellone:");
            System.out.println("\t0 - Nessuna preferenza");
            int i = 1;
            while(rs.next()){
                System.out.println("\t" + i + " - " + rs.getString("type_name") + ": " + rs.getInt("count"));
                i++;
            }
        } catch (SQLException s){
            throw new RuntimeException("Problemi a stabilire la connessione");
        }
        return true;
    }
}
