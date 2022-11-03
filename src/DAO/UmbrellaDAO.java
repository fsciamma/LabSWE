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

    public static void getUmbrella(String query, Umbrella u) throws SQLException {
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
    }

    public static Umbrella findById(int id) throws SQLException{
        Umbrella u = new Umbrella();
        String query = "select * from ombrellone where ombrelloneid = " + id;
        getUmbrella(query, u);
        return u;
    }

    public void findByType(int typeId) throws SQLException {
        Umbrella u = new Umbrella();
        String query = "select * from ombrellone where tipo_ombrellone = " + typeId;
        getUmbrella(query, u);
    }

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

    public void checkAvailableUmbrella(LocalDate start_date, LocalDate end_date){
        String query = "select ombrellone.ombrelloneid" +
                " from ombrellone " +
                " except select ombrelloneid" +
                " from reservation" +
                " where start_date <= '" + end_date + "' and end_date >= '" + start_date + "'";
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            if(!rs.isBeforeFirst()){
                throw new RuntimeException("Non ci sono ombrelloni disponibili nelle date selezionate.");
            }
        } catch (SQLException s){
            throw new RuntimeException("Problemi a stabilire la connessione");
        }
    }
}
