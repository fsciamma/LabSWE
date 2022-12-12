package DAO;

import model.Gazebo;
import model.Asset;
import model.Umbrella;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class AssetDAO extends  BaseDAO{
    private static AssetDAO INSTANCE;

    private AssetDAO(){
        super();
    }

    public static AssetDAO getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new AssetDAO();
        }
        return INSTANCE;
    }

    public static String showReservedAssets(int resID) throws SQLException{
        String query = "select * from \"laZattera\".reserved_assets join" +
                " \"laZattera\".reservable_asset on reserved_assets.\"assetID\" = reservable_asset.\"assetID\" join" +
                " \"laZattera\".reservable_type on reservable_asset.\"asset_type\" = reservable_type.\"typeID\"" +
                " where \"reservationID\" = " + resID;
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            StringBuilder s = new StringBuilder("   Sono stati richiesti i seguenti asset:\n");
            while(rs.next()){
                s.append("\t- ")
                        .append(rs.getString("type_name"))
                        .append(" N°").append(rs.getString("sub_classID"))
                        .append(", dal ").append(rs.getDate("start_date"))
                        .append(" al ").append(rs.getDate("end_date"))
                        .append(AddOnDAO.showAssociatedAddOns(rs.getInt("reservedID"))) //TODO forse va fatta una classe reservableAddOnDAO?
                        .append("\n");
            }
            return s.toString();
        }
    }



    /**
     * Metodo che fetcha un RA dal database secondo la query immessa.
     * @param query: query per la ricerca del RA
     * @return RA: Istanza del RA cercato dalla query nel Database
     */
    public Asset getRA(String query) throws SQLException {
        int type = 0, sub_classID = 0;
        BigDecimal price = BigDecimal.ZERO;
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){ //TODO nb: la query passata deve essere un join tra ReservableAsset e ReservableType
                type = rs.getInt("asset_type");
                sub_classID = rs.getInt("\"sub_classID\"");
                price = rs.getBigDecimal("price");
            }
        }

        switch (type){
            case 1 -> {
                return new Umbrella(sub_classID, price);
            }
            case 2 -> {
                return new Gazebo(sub_classID, price);
            }
            default -> throw new SQLException("L'asset cercato non è stato trovato");
        }
    }

    public Asset findByID(int ID) throws SQLException {
        String query = "select * from \"laZattera\".reservable_asset a join reservable_type b on a.asset_type = b.\"typeID\"" +
                " where \"assetID\" = " + ID;
        return getRA(query);
    }

    /*metodi per la tabella reservable_type*/

    public String fecthType(int fav_type) throws SQLException {
        String query = "select type_name from \"laZattera\".reservable_type where \"typeID\" =" + fav_type;
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            if(rs.isBeforeFirst()){
                return rs.getString("type_name");
            }
            else {
                throw new InputMismatchException();
            }
        }
    }
    //public ArrayList<Integer> getAvailableAssets(int type){
    //    ArrayList<Integer> availableUmbrellas;
//
    //    String query = "select *" +
    //            " from reservable_asset join availableUmbrellas on ombrellone.ombrelloneid = availableUmbrellas.ombrelloneid";
    //    if(type != 0){
    //        query = query + " where ombrellone.tipo_ombrellone = " + type;
    //    }
    //    query = query + " order by ombrellone.ombrelloneid asc";
    //    availableUmbrellas = printAvailableAssets(query);
    //    return availableUmbrellas;
    //}
//
    //private ArrayList<Integer> printAvailableAssets(String query){
    //    ArrayList<Integer> availableAssets = new ArrayList<>();
    //    try(Statement stmt = conn.createStatement()){
    //        ResultSet rs = stmt.executeQuery(query);
    //        while(rs.next()){
    //            availableUmbrellas.add(rs.getInt("ombrelloneid"));
    //            System.out.println(rs.getInt("ombrelloneid"));
    //        }
    //    } catch (SQLException e) {
    //        throw new RuntimeException(e);
    //    }
    //    return availableUmbrellas;
    //}
//

    public void showTypeTable() throws SQLException {
        String query = "select * from \"laZattera\".reservable_type";
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

    public ArrayList<Integer> checkAvailability(LocalDate start_date, LocalDate end_date, int fav_type) {
        String view_query = "create or replace view availableAssets as select assetID from \"laZattera\".reservable_asset " +
                "except " +
                "select assetID from \"laZattera\".reserved_assets where start_date <= '" + end_date + "' and end_date >= '" + start_date + "'";

        String query = "select \"assetID\", asset_type, type_name, \"sub_classID\", price from \"laZattera\".availableAssets a " +
                " right join \"laZattera\".reservable_asset b on a.\"assetID\" = b.\"assetID\"" +
                " join \"laZattera\".reservable_type c on b.asset_type = c.\"typeID\"";
        if(fav_type > 0){
            query = query + " where b.asset_type = " + fav_type;
        }

        ArrayList <Integer> available = new ArrayList<>();

        try(Statement stmt = conn.createStatement()){
            stmt.execute(view_query);
            if(!stmt.executeQuery("select * from \"laZattera\".availableAssets").next()){
                throw new RuntimeException("Non ci sono asset disponibili per le date selezionate.");
            }
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                int ID = rs.getInt("\"assetID\"");
                String name = rs.getString("type_name");
                int sub = rs.getInt("\"sub_classID\"");
                BigDecimal price = rs.getBigDecimal("price");
                System.out.println("Seleziona " + ID + " per: " + name + " - N°" + sub + " - " + price + "€ al giorno");
                available.add(ID);
            }
        } catch (SQLException s){
            System.err.println("Errore nella connesione alla tabella.");
        }
        return available;
    }
}
