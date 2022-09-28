package Calendario;

import java.util.ArrayList;
import java.util.Set;

public class Calendario{
    private ArrayList<Set<Integer>> giorniOmbrelloniOccupati; //per ogni giorno della stagione estiva indica quali ombrelloni sono occupati

    public Calendario(){
        giorniOmbrelloniOccupati = new ArrayList<>();
    }
}