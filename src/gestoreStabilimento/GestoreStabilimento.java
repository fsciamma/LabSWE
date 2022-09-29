package gestoreStabilimento;

import Calendario.Calendario;
import Prenotazione.Prenotazione;

import java.util.ArrayList;

public class GestoreStabilimento{
    private String _nomeBagno;
    private int _ombrelloniTot;
    private ArrayList<Prenotazione> _elencoPrenotazioni;
    private Calendario _myCalendar;

    private GestoreStabilimento(String name){
        this._nomeBagno = name;
        this._elencoPrenotazioni = new ArrayList<>();
        this._myCalendar = new Calendario();
    }

    public static GestoreStabilimento newBuilder(String name){
        return new GestoreStabilimento(name);
    }

    public GestoreStabilimento _ombrelloniTot(int maxOmbrelloni){
        this._ombrelloniTot = maxOmbrelloni;
        return this;
    }
    //TODO vanno aggiunti anche i campi _elencoPrenotazioni e _myCalendar al builder? Oppure basta inizializzarli quando creo uno stabiliimento?
}