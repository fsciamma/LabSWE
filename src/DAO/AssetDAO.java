package DAO;

import model.*;

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
                        .append(AddOnDAO.showAssociatedAddOns(rs.getInt("reservedID")))
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
        int type = 0, resID = 0, sub_classID = 0;
        BigDecimal price = BigDecimal.ZERO;
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){ //nb: la query passata deve essere un join tra ReservableAsset e ReservableType
                resID = rs.getInt("assetID");
                type = rs.getInt("asset_type");
                sub_classID = rs.getInt("sub_classID");
                price = rs.getBigDecimal("price");
            }
        }

        switch (type){
            case 1 -> {
                return new Umbrella(resID, sub_classID, price);
            }
            case 2 -> {
                return new Gazebo(resID, sub_classID, price);
            }
            default -> throw new SQLException("L'asset cercato non è stato trovato");
        }
    }

    public Asset findByID(int ID) throws SQLException {
        String query = "select * from \"laZattera\".reservable_asset a join \"laZattera\".reservable_type b on a.asset_type = b.\"typeID\"" +
                " where \"assetID\" = " + ID;
        return getRA(query);
    }

    /*metodi per la tabella reservable_type*/

    public String fecthType(int fav_type) throws SQLException {
        String query = "select type_name from \"laZattera\".reservable_type where \"typeID\" =" + fav_type;
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

    public ArrayList<Asset> checkAvailability(LocalDate start_date, LocalDate end_date, int fav_type) {
        String view_query = "create or replace view \"laZattera\".availableAssets as select \"assetID\" from \"laZattera\".reservable_asset " +
                "except " +
                "select \"assetID\" from \"laZattera\".reserved_assets where start_date <= '" + end_date + "' and end_date >= '" + start_date + "'";

        String query = "select a.\"assetID\", asset_type, type_name, \"sub_classID\", price from \"laZattera\".availableAssets a " +
                " left join \"laZattera\".reservable_asset b on a.\"assetID\" = b.\"assetID\"" +
                " join \"laZattera\".reservable_type c on b.asset_type = c.\"typeID\"";
        if(fav_type > 0){
            query = query + " where b.asset_type = " + fav_type;
        }
        query = query + "order by \"assetID\"";
        ArrayList <Asset> available = new ArrayList<>();

        try(Statement stmt = conn.createStatement()){
            stmt.execute(view_query);
            if(!stmt.executeQuery("select * from \"laZattera\".availableAssets").next()){
                throw new RuntimeException("Non ci sono asset disponibili per le date selezionate.");
            }
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                int ID = rs.getInt("assetID");
                available.add(findByID(ID));
            }
        } catch (SQLException s){
            throw new RuntimeException("Errore nella connesione alla tabella");
        }
        return available;
    }

    /**
     * Ricerca i ReservedAsset associati ad una specifica prenotazione. Per ogni ReservedAsset esegue una call a getReservedAddOns per popolarne la lista di ReservedAddOns
     * @param reservationID identificativo della prenotazione
     * @return la lista di ReservedAsset associata alla prenotazione
     */
    public ArrayList<ReservedAsset> getReservedAssets(int reservationID) {
        String query = "select reserved_assets.\"assetID\", asset_type, reservable_asset.\"sub_classID\", reservable_type.price, reserved_assets.start_date, reserved_assets.end_date, reserved_assets.\"reservedID\"" +
                " from \"laZattera\".reserved_assets" +
                " join \"laZattera\".reservable_asset on \"laZattera\".reserved_assets.\"assetID\" = \"laZattera\".reservable_asset.\"assetID\"" +
                " join \"laZattera\".reservable_type on \"laZattera\".reservable_asset.asset_type = \"laZattera\".reservable_type.\"typeID\"" +
                " where \"laZattera\".reserved_assets.\"reservationID\" = " + reservationID;
        ArrayList<ReservedAsset> myList = new ArrayList<>();
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                switch (rs.getInt("asset_type")){
                    case 1 -> {
                        ReservedAsset tmp = new ReservedAsset(new Umbrella(rs.getInt("assetID"), rs.getInt("sub_classID"), rs.getBigDecimal("price")), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate());
                        tmp.setAdd_ons(AddOnDAO.getINSTANCE().getReservedAddOns(rs.getInt("reservedID")));
                        myList.add(tmp);
                    }
                    case 2 -> {
                        ReservedAsset tmp = new ReservedAsset(new Gazebo(rs.getInt("assetID"), rs.getInt("sub_classID"), rs.getBigDecimal("price")), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate());
                        tmp.setAdd_ons(AddOnDAO.getINSTANCE().getReservedAddOns(rs.getInt("reservedID")));
                        myList.add(tmp);
                    }
                    default -> throw new SQLException("L'asset cercato non è stato trovato");
                }
            }
        } catch(SQLException s){
            System.err.println(s.getMessage());
        }
        return myList;
    }

    public void totalDestruction(int resCode) {
        String query = "select * from \"laZattera\".reserved_assets where \"reservationID\" = " + resCode;
        try(Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                AddOnDAO.getINSTANCE().totalDestruction(rs.getInt("reservedID"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String queryDelete = "delete from \"laZattera\".reserved_assets where \"reservationID\" = " + resCode;
        try(Statement stmt = conn.createStatement()){
            stmt.executeUpdate(queryDelete);
            System.out.println("Gli asset sono stati cancellati correttamente");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
