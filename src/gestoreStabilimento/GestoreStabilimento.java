package gestoreStabilimento;

import Calendario.Calendario;
import Prenotazione.Prenotazione;

import java.util.ArrayList;

public class GestoreStabilimento{
    private String _nomeBagno;
    private int _ombrelloniTot;
    private ArrayList<Prenotazione> _elencoPrenotazioni;
    private Calendario myCalendar;

    public GestoreStabilimento(String name, int maxOmbrelloni){
        this._nomeBagno = name;
        this._ombrelloniTot = maxOmbrelloni;
        this._elencoPrenotazioni = new ArrayList<>();
        myCalendar = new Calendario();
    }
}