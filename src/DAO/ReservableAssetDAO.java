package DAO;

import model.Gazebo;
import model.ReservableAsset;
import model.Umbrella;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class ReservableAssetDAO extends  BaseDAO{
    private static ReservableAssetDAO INSTANCE;

    private ReservableAssetDAO(){
        super();
    }

    public static ReservableAssetDAO getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new ReservableAssetDAO();
        }
        return INSTANCE;
    }

    /**
     * Metodo che fetcha un RA dal database secondo la query immessa.
     * @param query: query per la ricerca del RA
     * @return RA: Istanza del RA cercato dalla query nel Database
     */
    public ReservableAsset getRA(String query) throws SQLException {
        int type = 0, sub_classID = 0;
        BigDecimal price = BigDecimal.ZERO;
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){ //TODO nb: la query passata deve essere un join tra ReservableAsset e ReservableType
                type = rs.getInt("asset_type");
                sub_classID = rs.getInt("sub_classID");
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

/*metodi per la tabella reservable_type*/

    public String fecthType(int fav_type) throws SQLException {
        String query = "select type_name from reservable_type where typeID =" + fav_type;
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

    public ArrayList<Integer> getAvailableAssets(int type){
        ArrayList<Integer> availableUmbrellas;

        String query = "select *" +
                " from reservable_asset join availableUmbrellas on ombrellone.ombrelloneid = availableUmbrellas.ombrelloneid";
        if(type != 0){
            query = query + " where ombrellone.tipo_ombrellone = " + type;
        }
        query = query + " order by ombrellone.ombrelloneid asc";
        availableUmbrellas = printAvailableAssets(query);
        return availableUmbrellas;
    }

    private ArrayList<Integer> printAvailableAssets(String query){
        ArrayList<Integer> availableAssets = new ArrayList<>();
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

    public void showTypeTable() throws SQLException {
        String query = "select * from reservable_type";
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                int ID = rs.getInt("typeID");
                String name = rs.getString("type_name");
                BigDecimal price = rs.getBigDecimal("price");
                System.out.println("\t" + ID + " - " + name + ", prezzo a partire da " + price + "");
            }
        }
    }

    public void checkAvailability(LocalDate start_date, LocalDate end_date) throws RuntimeException {
        String query = "select assetID from reservable_asset " +
                "except " +
                "selct ombrelloneID from reserved_assets where start_date <= '" + end_date + "' and end_date >= '" + start_date + "'";

        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            if(!rs.isBeforeFirst()){
                throw new RuntimeException("Non ci sono ombrelloni disponibili per le date selezionate.");
            }
        } catch (SQLException s){
            System.err.println("Errore nella connesione alla tabella.");
        }
    }
}
