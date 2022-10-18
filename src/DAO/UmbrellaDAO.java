package DAO;

import model.Umbrella;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public Umbrella getUmbrella(int id) throws SQLException {
        Umbrella u = new Umbrella();
        String query = "select * from ombrellone where ombrelloneid = " + id;
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                u.setUmbrellaId(id);
                u.setValues(rs.getInt("tipo_ombrellone"));
            }
        }
        //TODO ma ci va anche la clausola catch?
        if(u.getUmbrellaId() == 0){
            throw new SQLException();
        }
        return u;
    }
}