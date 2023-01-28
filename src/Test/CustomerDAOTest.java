package Test;

import DAO.BaseDAO;
import DAO.CustomerDAO;
import model.Customer;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import static org.junit.jupiter.api.Assertions.*;

class CustomerDAOTest{

    static CustomerDAO cDAO = CustomerDAO.getINSTANCE();
    static String email = "g.g@gg.com";
    public static int countElements(){
        int count = 0;
        String query = "select count(*) as countCustomers from \"laZattera\".customer";
        try(Statement stmt = cDAO.getConnection().createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                count = rs.getInt("countCustomers");
            }
        } catch (SQLException e) {
            System.err.println("Errore nell'esecuzione del test");
        }
        return count;
    }

    @BeforeAll
    public  static void setup(){
        cDAO = CustomerDAO.getINSTANCE();

        String query = "delete from \"laZattera\".customer where email = '" + email + "'";
        try(Statement stmt = cDAO.getConnection().createStatement()){
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Errore nell'esecuzione del test");
        }
    }

    @Test
    public void testCustomerHandling() throws SQLException {
        Customer c = new Customer();
        c.set_email(email);
        c.set_first_name("Giovanni");
        c.set_last_name("Gentile");
        int prec = countElements();
        cDAO.addNewCustomer(c);

        assertEquals(prec + 1, countElements());

        Customer c2 = new Customer();
        c2.copy(c);
        cDAO.findAll();

        c = cDAO.findByEMail(email);

        assertEquals(c.get_email(), c2.get_email());
        assertEquals(c.get_first_name(), c2.get_first_name());
        assertEquals(c.get_last_name(), c2.get_last_name());

        c2.set_first_name("Giacomo");
        cDAO.updateInfo(c2);

        c2 = cDAO.findByEMail(email);
        assertEquals(c.get_email(), c2.get_email());
        assertNotEquals(c.get_first_name(), c2.get_first_name());
        assertEquals(c.get_last_name(), c2.get_last_name());

        //TODO assertThrows(IllegalArgumentException.class, c::set_email);
    }

    @AfterAll
    public static void revert(){
        String query = "delete from \"laZattera\".customer where email = '" + email + "'";
        try(Statement stmt = cDAO.getConnection().createStatement()){
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Errore nell'esecuzione del test");
        }
    }
}