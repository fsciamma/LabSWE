package model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Deckchair extends AddOn {

    private int deckchairId;

    private BigDecimal daily_price;

    public Deckchair() {
    }

    public Deckchair(int aoId, int dcId, BigDecimal daily_price) {
        setAdd_onId(aoId);
        this.deckchairId = dcId;
        this.daily_price = daily_price;
    }

    public int getDeckchairId() {
        return deckchairId;
    }

    public void setDeckchairId(int deckchairId) {
        this.deckchairId = deckchairId;
    }

    @Override
    public BigDecimal getPrice() {
        return this.daily_price;
    }

    @Override
    public String toString(){
        return "Lettino N°" + this.deckchairId + ", prezzo per giornata: " + this.daily_price +".";
    }
}
