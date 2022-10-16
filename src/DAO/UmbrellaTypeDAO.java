package DAO;

import model.TypeDetails;
import model.UmbrellaType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UmbrellaTypeDAO extends BaseDAO {
    private static UmbrellaTypeDAO INSTANCE;
    private UmbrellaTypeDAO(){
        super();
    }

    public static UmbrellaTypeDAO getInstance(){
        if(INSTANCE == null){
            INSTANCE = new UmbrellaTypeDAO();
        }
        return INSTANCE;
    }

    public UmbrellaType getUTypes() throws SQLException {
        UmbrellaType u = UmbrellaType.getInstance();
        String query = "select * from tipoOmbrellone";
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                TypeDetails tD = new TypeDetails();
                tD.setTypeName(rs.getString("type_name"));
                tD.setTypePrice(rs.getFloat("daily_price"));
                u.getUTypeMap().put(rs.getInt("typeid"), tD);
            }
        }
        return u;
    }
}
