package Test;

import DAO.*;
import model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.*;

class InvoiceTest {
    static InvoiceDAO iDAO;
    static ReservationDAO rDAO;
    static String customer = "test.email@g.com";
    static Asset asset;
    static Asset asset2;
    static AddOn addOn;

    static int invCountElements(){
        int count = 0;
        String query = "select count(*) as countInvoices from \"laZattera\".invoice";
        try(Statement stmt = iDAO.getConnection().createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                count = rs.getInt("countInvoices");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return count;
    }

    @BeforeAll
    public static void setUp() throws SQLException {
        iDAO = InvoiceDAO.getINSTANCE();
        rDAO = ReservationDAO.getInstance();
        AssetDAO aDAO = AssetDAO.getINSTANCE();
        AddOnDAO aoDAO = AddOnDAO.getINSTANCE();
        CustomerDAO cDAO = CustomerDAO.getINSTANCE();
        String query = "delete from \"laZattera\".customer where email = '" + customer + "'";
        try(Statement stmt = cDAO.getConnection().createStatement()){
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        asset = aDAO.findByID(1);
        asset2 = aDAO.findByID(2);
        addOn = aoDAO.findByID(1);

        Customer c;
        c = new Customer();
        c.set_email(customer);
        c.set_first_name("Test");
        c.set_last_name("Test");
        cDAO.addNewCustomer(c);
    }

    @Test
    public void testInvoiceHandling() throws SQLException {
        //Creo tutti gli oggetti necessari per eseguire i test
        Reservation r = Reservation.createNewReservation(customer);
        Invoice i = new Invoice();

        //Test, verifico il corretto calcolo del prezzo
        //Calcolo i giorni effettivi della durata della prenotazione
        long interval = DAYS.between(LocalDate.of(2024,6,18), LocalDate.of(2024,6,20)) + 1;

        //Compongo la prenotazione con gli oggetti scelti e calcolo il prezzo totale
        ReservedAsset a = new ReservedAsset(asset, LocalDate.of(2024,6,18), LocalDate.of(2024,6,20));
        ReservedAddOn ao = new ReservedAddOn(addOn, LocalDate.of(2024,6,18), LocalDate.of(2024,6,20));
        a.addReservedAddOn(ao);
        r.addReservedAsset(a);
        r.compute_total();

        assertEquals((asset.getPrice().longValue() * interval) + (addOn.getPrice().longValue() * interval), r.getTotal_price().longValue());

        //Test, verifica aggiunta con successo di un elemento
        //Aggiorno l'invoice e aggiungo la prenotazione al DB per passare ai test sul database
        i.setInvoice_amount(r.getTotal_price());
        int res_id = rDAO.addNewReservation(r);
        i.setInvoiceID(res_id);

        int prev = invCountElements();
        iDAO.addNewInvoice(i);

        assertEquals(prev + 1, invCountElements());

        //Test, verifica funzionamento dei metodi di update
        //Aggiungo un nuovo asset alla prenotazione e aggiorno il prezzo sull'invoice
        ReservedAsset a2 = new ReservedAsset(asset2, LocalDate.of(2024,6,18), LocalDate.of(2024,6,20));
        r.addReservedAsset(a2);
        iDAO.updatePrice(res_id, a2.getPrice());

        Invoice inv = iDAO.findByInvoiceID(res_id);
        assertEquals(i.getInvoice_amount().longValue() + (asset2.getPrice().longValue() * interval) , inv.getInvoice_amount().longValue());

        i.setPaid(true);
        iDAO.updateInovice(i);
        inv = iDAO.findByInvoiceID(res_id);
        assertTrue(inv.isPaid());

        //Test, verifica cancellazione con successo di un elemento dal DB
        iDAO.deleteInvoice(res_id);
        assertEquals(prev, invCountElements());
        rDAO.totalDestruction(res_id);
    }

    @AfterAll
    public static void tearDown(){
        CustomerDAO cDAO = CustomerDAO.getINSTANCE();
        String query = "delete from \"laZattera\".customer where email = '" + customer + "'";
        try(Statement stmt = cDAO.getConnection().createStatement()){
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

}