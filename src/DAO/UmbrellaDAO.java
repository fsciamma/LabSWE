package DAO;

import model.Reservation;
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

    public static Umbrella getUmbrella(String query, Umbrella u) throws SQLException {
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                u.setUmbrellaId(rs.getInt("ombrelloneid"));
                u.setValues(rs.getInt("tipo_ombrellone"));
            }
        }
        //TODO ma ci va anche la clausola catch?
        if(u.getUmbrellaId() == 0){
            throw new SQLException("L'ombrellone non è stato trovato");
        }
        System.out.println(u);
        return u;
    }

    public static Umbrella findById(int id) throws SQLException{
        Umbrella u = new Umbrella();
        String query = "select * from ombrellone where ombrelloneid = " + id;
        try{
            getUmbrella(query, u);
        }catch(SQLException e){
            System.err.println("Non sono stati trovati ombrelloni con i dati forniti");
        }
        return u;
    }

    public Umbrella findByType(int typeId) throws SQLException {
        Umbrella u = new Umbrella();
        String query = "select * from ombrellone where tipo_ombrellone = " + typeId;
        try{
            getUmbrella(query, u);
        }catch(SQLException e){
            System.err.println("Non sono stati trovati ombrelloni con i dati forniti");
        }
        return u;
    }

    //TODO valutare se passare l'oggetto Reservation o le due date di inizio e fine
    public ArrayList<Integer> getAvailableUmbrellas(Reservation res, int type) throws SQLException{
        LocalDate requested_start_date = res.getStart_date();
        LocalDate requested_end_date = res.getEnd_date();
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
            throw new SQLException("Non ci sono ombrelloni disponibili in questo periodo");
        }
        return availableUmbrellas;
    }
}
