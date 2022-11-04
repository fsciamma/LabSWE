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
        System.out.println(u); //TODO prob da rimuovere
        return u;
    }

    public Umbrella findById(int id) throws SQLException{
        String query = "select * from ombrellone where ombrelloneid = " + id;
        return getUmbrella(query);
    }

    public void findByType(int typeId) throws SQLException {
        Umbrella u = new Umbrella();
        String query = "select * from ombrellone where tipo_ombrellone = " + typeId;
        getUmbrella(query);
    }

    //TODO questo potrebbe già chiamare la view availableUmbrella?
    public ArrayList<Integer> getAvailableUmbrellas(LocalDate requested_start_date, LocalDate requested_end_date, int type) throws SQLException{
        ArrayList<Integer> availableUmbrellas;

        String query1 = "select ombrellone.ombrelloneid" +
                " from ombrellone join tipoombrellone on ombrellone.tipo_ombrellone = tipoombrellone.typeid";
        if(type != 0){
            query1 = query1 + " where ombrellone.tipo_ombrellone = " + type;
        }
        String query2 = " except select ombrelloneid" +
                " from reservation" +
                " where start_date <= '" + requested_end_date + "' and end_date >= '" + requested_start_date + "'" +
                " order by ombrelloneid asc";
        availableUmbrellas = showAvailableUmbrellas(query1+query2);
        return availableUmbrellas;
    }

    private ArrayList<Integer> showAvailableUmbrellas(String query) throws SQLException {
        ArrayList<Integer> availableUmbrellas = new ArrayList<>();
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                availableUmbrellas.add(rs.getInt("ombrelloneid"));
                System.out.println(rs.getInt("ombrelloneid"));
            }
        }
        if(availableUmbrellas.isEmpty()){
            throw new SQLException("Non ci sono ombrelloni del tipo selezionato disponibili in questo periodo");
        }
        return availableUmbrellas;
    }

    /**
     * Si interfaccia col database, fornendo informazioni sulle disponibilità singole dei tipi di ombrelloni
     * @param req_start_date La LocalDate di inizio prenotazione
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
