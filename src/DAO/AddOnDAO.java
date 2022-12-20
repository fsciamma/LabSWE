package DAO;

import model.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class AddOnDAO extends BaseDAO{
    private static AddOnDAO INSTANCE;

    private AddOnDAO(){
        super();
    }

    public static AddOnDAO getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new AddOnDAO();
        }
        return INSTANCE;
    }

    static String showAssociatedAddOns(int reservedID) throws SQLException {
        String query = "select * from \"laZattera\".reserved_add_on" +
                " join \"laZattera\".add_on on reserved_add_on.\"add_onID\" = add_on.\"add_onID\"" +
                " join \"laZattera\".add_on_type on add_on.add_on_type = add_on_type.\"typeID\"" +
                " where \"reserved_assetsID\" = " + reservedID;
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if(rs.isBeforeFirst()) {
                StringBuilder s = new StringBuilder("\n\t   con le seguenti aggiunte:\n");
                while (rs.next()) {
                    s.append("\t   - ")
                            .append(rs.getString("type_name"))
                            .append(" N°").append(rs.getString("sub_classID"))
                            .append(", dal ").append(rs.getDate("start_date"))
                            .append(" al ").append(rs.getDate("end_date"))
                            .append("\n");
                }
                return s.toString();
            }
            return "\n";
        }
    }

    private AddOn getAO(String query) throws SQLException{
        int aoID = 0, type = 0, sub_classID = 0;
        BigDecimal price = BigDecimal.ZERO;
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                aoID = rs.getInt("add_onID");
                type = rs.getInt("add_on_type");
                sub_classID = rs.getInt("sub_classID");
                price = rs.getBigDecimal("price");
            }
        }

        switch (type){
            case 1 -> {
                return new Chair(aoID, sub_classID, price);
            }
            case 2 -> {
                return new Deckchair(aoID, sub_classID, price);
            }
            case 3 -> {
                return new Beachbed(aoID, sub_classID, price);
            }
            case 4 -> {
                return new Booth(aoID, sub_classID, price);
            }
            default -> throw new SQLException("L'add on non è stato trovato");
        }
    }

    public void showTypeTable() throws SQLException {
        String query = "select * from \"laZattera\".add_on_type";
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                int ID = rs.getInt("typeID");
                String name = rs.getString("type_name");
                BigDecimal price = rs.getBigDecimal("price");
                System.out.println("\t" + ID + " - " + name + ", prezzo a partire da " + price + "€");
            }
        }
    }

    public String fecthType(int favType) throws SQLException {
        String query = "select type_name from \"laZattera\".add_on_type where \"typeID\" =" + favType;
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            if(rs.isBeforeFirst()){
                rs.next();
                return rs.getString("type_name");
            }
            else {
                throw new InputMismatchException();
            }
        }
    }

    public ArrayList<AddOn> checkAvailability(LocalDate startDate, LocalDate endDate, int chosenType) {
        String view_query = "create or replace view \"laZattera\".availableAddOn as select \"add_onID\" from \"laZattera\".add_on " +
                "except " +
                "select \"add_onID\" from \"laZattera\".reserved_add_on where start_date <= '" + endDate + "' and end_date >= '" + startDate + "'";

        String query = "select a.\"add_onID\", add_on_type, type_name, \"sub_classID\", price from \"laZattera\".availableAddOn a " +
                " left join \"laZattera\".add_on b on a.\"add_onID\" = b.\"add_onID\"" +
                " join \"laZattera\".add_on_type c on b.add_on_type = c.\"typeID\"";
        if(chosenType > 0){
            query = query + " where b.add_on_type = " + chosenType;
        }
        query = query + "order by \"add_onID\"";
        ArrayList <AddOn> available = new ArrayList<>();

        try(Statement stmt = conn.createStatement()){
            stmt.execute(view_query);
            if(!stmt.executeQuery("select * from \"laZattera\".availableAddOn").next()){
                throw new RuntimeException("Non ci sono add on disponibili per le date selezionate.");
            }
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                int ID = rs.getInt("add_onID");
                available.add(findByID(ID));
            }
        } catch (SQLException s){
            throw new RuntimeException("Errore nella connesione alla tabella.");
        }
        return available;
    }

    public AddOn findByID(int ID) throws SQLException{
        String query = "select * from \"laZattera\".add_on a join \"laZattera\".add_on_type b on a.add_on_type = b.\"typeID\"" +
                " where \"add_onID\" = " + ID;
        return getAO(query);
    }
}
