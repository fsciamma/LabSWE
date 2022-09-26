import java.util.Observable;

public class GestoreMagazzino extends Observable {  //TODO: Probabile necessaria mplementazione del pattern observer ad hoc
    private int _nSdraio;

    private int _nLettini;

    private int _nSedie;

    private static GestoreMagazzino _instance = null;

    public GestoreMagazzino() {
    }

    public GestoreMagazzino(int _nSdraio, int _nLettini, int _nSedie) {
        this._nSdraio = _nSdraio;
        this._nLettini = _nLettini;
        this._nSedie = _nSedie;
    }

    //TODO: Singleton generale, da gestire caso di un magazzino per bagno
    public static GestoreMagazzino getInstance(){
        if(_instance == null){
            _instance = new GestoreMagazzino();
        }
        return _instance;
    }

    public int get_nSdraio() {
        return _nSdraio;
    }

    public int get_nLettini() {
        return _nLettini;
    }

    public int get_nSedie() {
        return _nSedie;
    }

    //TODO: metodi di gestione acquisizione risorse. Accorpato al calendario?
}
