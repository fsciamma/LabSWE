package Test;

import DAO.AddOnDAO;
import DAO.AssetDAO;
import DAO.CustomerDAO;
import DAO.ReservationDAO;
import model.*;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    static ReservationDAO rDAO;
    static String customer = "test.email@g.com";
    static Asset asset;
    static AddOn addOn;
    static int resCountElements(){
        int count = 0;
        String query = "select count(*) as countReservations from \"laZattera\".reservation";
        try(Statement stmt = rDAO.getConnection().createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                count = rs.getInt("countReservations");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return count;
    }

    static int rAssetCountElements(){
        int count = 0;
        String query = "select count(*) as countResAssets from \"laZattera\".reserved_assets";
        try(Statement stmt = rDAO.getConnection().createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                count = rs.getInt("countResAssets");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return count;
    }

    static int rAddOnCountElements(){
        int count = 0;
        String query = "select count(*) as countResAddOns from \"laZattera\".reserved_add_on";
        try(Statement stmt = rDAO.getConnection().createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                count = rs.getInt("countResAddOns");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return count;
    }

    @BeforeAll
    public static void setUp() throws SQLException {
        rDAO = ReservationDAO.getInstance();
        CustomerDAO cDAO = CustomerDAO.getINSTANCE();

        String query = "delete from \"laZattera\".customer where email = '" + customer + "'";
        try(Statement stmt = cDAO.getConnection().createStatement()){
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        Customer c;
        c = new Customer();
        c.set_email(customer);
        c.set_first_name("Test");
        c.set_last_name("Test");
        cDAO.addNewCustomer(c);

        asset = AssetDAO.getINSTANCE().findByID(20);
        addOn = AddOnDAO.getINSTANCE().findByID(28);
    }

    @Test
    public void testReservationHandling() throws SQLException {
        //Setup per test
        Reservation r = Reservation.createNewReservation(customer);

        ReservedAsset rAsset1 = new ReservedAsset(asset, LocalDate.of(2023,6,18), LocalDate.of(2023,6,20));
        ReservedAddOn rAddOn1 = new ReservedAddOn(addOn, LocalDate.of(2023, 6, 18), LocalDate.of(2023, 6, 18));

        rAsset1.addReservedAddOn(rAddOn1);

        r.addReservedAsset(rAsset1);

        //Test aggiunta elementi al DB
        int r_prev = resCountElements();
        int res_id = rDAO.addNewReservation(r);
        int a_prev = rAssetCountElements();
        int asset_id = rDAO.addNewReserved_asset(res_id, rAsset1);
        int ao_prev = rAddOnCountElements();
        rDAO.addNewReservedAddOn(asset_id, rAddOn1);

        //L'aggiunta al DB ha generato un id univoco
        assertNotEquals(r.getReservationId(), res_id);

        //Gli elementi sono stati aggiunti
        assertEquals(r_prev + 1, resCountElements());
        assertEquals(a_prev + 1, rAssetCountElements());
        assertEquals(ao_prev + 1, rAddOnCountElements());

        Reservation r2 = rDAO.findById(res_id);
        //Il cliente sulla prenotazione aggiunta corrisponde
        assertEquals(r.getCustomer(), r2.getCustomer());
        //La prenotazione aggiunta contiene gli asset corretti
        assertTrue(rDAO.getAssetsInReservation(res_id).contains(asset.getAssetId()));
        //L'asset aggiunto contiene i relativi AddOns
        assertTrue(rDAO.getReservedAddOns(asset_id).stream().anyMatch(o -> o.getAddon().getAdd_onId() == addOn.getAdd_onId()));


        //Test eliminazione elementi dal DB
        rDAO.totalDestruction(res_id);

        //La funzione ha effettivamente eliminato tutto
        assertThrows(SQLException.class, () -> rDAO.findById(res_id));
        assertEquals(r_prev, resCountElements());
        assertEquals(a_prev, rAssetCountElements());
        assertEquals(ao_prev, rAddOnCountElements());
    }

    @AfterAll
    public static void tearDown() {
        CustomerDAO cDAO = CustomerDAO.getINSTANCE();
        String query = "delete from \"laZattera\".customer where email = '" + customer + "'";
        try(Statement stmt = cDAO.getConnection().createStatement()){
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }



}