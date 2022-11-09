import BusinessLogic.BusinessLogic;
import DAO.UmbrellaTypeDAO;

public class LabSWE {
    public static void main(String[] args) {
        tableInit();
        BusinessLogic.mainMenu();
    }

    /**
     * Inizializza la tabella statica dei tipi di ombrelloni
     */
    private static void tableInit(){
        UmbrellaTypeDAO.getInstance().getUTypes();
    }
}