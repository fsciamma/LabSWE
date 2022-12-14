package model;

import java.math.BigDecimal;

public class Chair extends AddOn{

    private int chairId;

    private BigDecimal daily_price;

    public Chair() {}

    public Chair(int aoId, int chId, BigDecimal daily_price){
        setAdd_onId(aoId);
        this.chairId = chId;
        this.daily_price = daily_price;
    }

    public int getChairId() {
        return chairId;
    }

    public void setChairId(int chairId) {
        this.chairId = chairId;
    }

    @Override
    public BigDecimal getPrice() {
        return this.daily_price;
    }

    @Override
    public String toString(){
        return "Lettino N°" + this.chairId + ", prezzo per giornata: " + this.daily_price +".";
    }
}
