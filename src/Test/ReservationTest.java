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
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    static ReservationDAO rDAO;
    static AssetDAO aDAO;
    static AddOnDAO aoDAO;
    static String customer = "test.email@g.com";
    static Asset asset;
    static AddOn addOn;
    static AddOn addOn2;
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
        aDAO = AssetDAO.getINSTANCE();
        aoDAO = AddOnDAO.getINSTANCE();
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

        asset = aDAO.findByID(20);
        addOn = aoDAO.findByID(28);
        addOn2 = aoDAO.findByID(1);
    }

    @Test
    public void testReservationHandling() throws SQLException {
        //Setup specifico per test
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

    @Test
    void testReservedHandling() throws SQLException {
        //Setup specifico per test
        Reservation r = Reservation.createNewReservation(customer);

        ReservedAsset rAsset1 = new ReservedAsset(asset, LocalDate.of(2023,6,18), LocalDate.of(2023,6,20));
        ReservedAddOn rAddOn1 = new ReservedAddOn(addOn, LocalDate.of(2023, 6, 18), LocalDate.of(2023, 6, 18));
        ReservedAddOn rAddOn2 = new ReservedAddOn(addOn2, LocalDate.of(2023, 6, 18), LocalDate.of(2023, 6, 18));

        rAsset1.addReservedAddOn(rAddOn1);
        rAsset1.addReservedAddOn(rAddOn2);

        r.addReservedAsset(rAsset1);
        int res_id = rDAO.addNewReservation(r);
        int asset_id = rDAO.addNewReserved_asset(res_id, rAsset1);
        rDAO.addNewReservedAddOn(asset_id, rAddOn1);
        rDAO.addNewReservedAddOn(asset_id, rAddOn2);
        //Test, controllo che le singole funzioni di eliminazione di oggetti dal database funzionino correttamente
        assertEquals(2, rDAO.getReservedAddOns(asset_id).size());
        rDAO.deleteReservedAddOn(rAddOn2);
        assertFalse(rDAO.getReservedAddOns(asset_id).stream().anyMatch(o -> (o.getAddon().getAdd_onId() == addOn2.getAdd_onId()) && o.getStart_date().isEqual(rAddOn2.getStart_date())));
        rDAO.deleteReservedAddOn(asset_id);
        assertTrue(rDAO.getReservedAddOns(asset_id).isEmpty());
        rDAO.deleteReservedAsset(asset_id);
        assertTrue(rDAO.getReservedAssets(res_id).isEmpty());

        rDAO.deleteReservation(res_id);
    }

    @Test
    void availabilityHandling() throws SQLException {
        //Setup specifico per test
        Reservation r = Reservation.createNewReservation(customer);
        ReservedAsset rAsset = new ReservedAsset(asset, LocalDate.of(2023,6,18), LocalDate.of(2023,6,20));
        ReservedAddOn rAddOn = new ReservedAddOn(addOn, LocalDate.of(2023, 6, 18), LocalDate.of(2023, 6, 18));
        rAsset.addReservedAddOn(rAddOn);
        r.addReservedAsset(rAsset);
        //Test, coltrollo che la logica scritta per vincolare la prenotazione di asset sia corretta
        int res_id = rDAO.addNewReservation(r);
        int asset_id = rDAO.addNewReserved_asset(res_id, rAsset);
        rDAO.addNewReservedAddOn(asset_id, rAddOn);

        //Controllo gli asset rimasti nelle date esatte in cui ho prenotato l'asset
        ArrayList<Asset> a_remaining = aDAO.checkAvailability(LocalDate.of(2023,6,18), LocalDate.of(2023,6,20), 0);
        //Controllo gli add-on rimasti nelle date che intersecano quelle in cui ho prenotato l'asset
        ArrayList<AddOn> ao_remaining = aoDAO.checkAvailability(LocalDate.of(2023,6,18), LocalDate.of(2023,6,20), 0);

        assertTrue(a_remaining.stream().noneMatch(o -> o.getAssetId() == asset.getAssetId()));
        assertTrue(ao_remaining.stream().noneMatch(o -> o.getAdd_onId() == addOn.getAdd_onId()));

        rDAO.totalDestruction(res_id);
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