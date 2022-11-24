package DAO;

import model.Gazebo;
import model.ReservableAsset;
import model.Umbrella;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
    public static ReservableAsset getRA(String query) throws SQLException {
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
}
