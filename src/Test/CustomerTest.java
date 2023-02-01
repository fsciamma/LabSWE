package Test;

import DAO.CustomerDAO;
import DAO.ReservationDAO;
import model.Customer;
import model.Reservation;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    static CustomerDAO cDAO;
    static String email = "test.email@g.com";
    static int countElements(){
        int count = 0;
        String query = "select count(*) as countCustomers from \"laZattera\".customer";
        try(Statement stmt = cDAO.getConnection().createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                count = rs.getInt("countCustomers");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return count;
    }

    @BeforeAll
    public static void setUp(){
        cDAO = CustomerDAO.getINSTANCE();
        ReservationDAO rDAO = ReservationDAO.getInstance();

        String query = "delete from \"laZattera\".reservation where \"customerID\" = '" + email + "'";
        try(Statement stmt = rDAO.getConnection().createStatement()){
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        String query2 = "delete from \"laZattera\".customer where email = '" + email + "'";
        try(Statement stmt = cDAO.getConnection().createStatement()){
            stmt.executeUpdate(query2);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
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
    public static void tearDown(){
        String query = "delete from \"laZattera\".customer where email = '" + email + "'";
        try(Statement stmt = cDAO.getConnection().createStatement()){
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}