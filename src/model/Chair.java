package model;

import java.math.BigDecimal;

public class Chair extends AddOn{

    private int chairId;

    public Chair() {
        super();
    }

    public Chair(int aoId, int chId, BigDecimal daily_price){
        super(aoId, daily_price);
        this.chairId = chId;
    }

    public int getChairId() {
        return chairId;
    }

    public void setChairId(int chairId) {
        this.chairId = chairId;
    }

    @Override
    public String toString(){
        return "Regista N°" + this.chairId + ", prezzo per giornata: " + this.price +"€.";
    }
}
