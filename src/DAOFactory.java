import DAO.*;

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


    public InvoiceDAO createInvoiceDAO(){
        return InvoiceDAO.getINSTANCE();
    }


    //TODO aggiungere gli altri metodi via via che vengono creati i rispettivi DAO
}
