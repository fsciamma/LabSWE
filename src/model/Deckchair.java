package model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Deckchair extends AddOn {

    private int deckchairId;

    public Deckchair() {
        super();
    }

    public Deckchair(int aoId, int dcId, BigDecimal daily_price) {
        super(aoId, daily_price);
        this.deckchairId = dcId;
    }

    public int getDeckchairId() {
        return deckchairId;
    }

    public void setDeckchairId(int deckchairId) {
        this.deckchairId = deckchairId;
    }

    @Override
    public String toString(){
        return "Lettino N°" + this.deckchairId + ", prezzo per giornata: " + getPrice() +".";
    }
}
