package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
    }
}
