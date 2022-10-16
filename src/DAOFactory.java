import DAO.CustomerDAO;
import DAO.InvoiceDAO;
import DAO.ReservationDAO;
import DAO.UmbrellaTypeDAO;

public class DAOFactory {
    private static DAOFactory INSTANCE;
    private DAOFactory(){}

    public static DAOFactory getFactory(){
        if(INSTANCE == null){
            INSTANCE = new DAOFactory();
        }
        return INSTANCE;
    }

    public CustomerDAO createCustomerDAO(){
        return CustomerDAO.getINSTANCE();
    }

    public ReservationDAO createResDAO(){
        return ReservationDAO.getInstance();
    }

    public UmbrellaTypeDAO createUtypeDAO(){
        return UmbrellaTypeDAO.getInstance();
    }

    public InvoiceDAO createInvoiceDAO(){
        return InvoiceDAO.getINSTANCE();
    }
    //TODO aggiungere gli altri metodi via via che vengono creati i rispettivi DAO
}
