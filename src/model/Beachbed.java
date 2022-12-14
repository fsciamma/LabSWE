package model;

import java.math.BigDecimal;

public class Beachbed extends AddOn{

    private int beachbedId;

    private BigDecimal daily_price;

    public Beachbed() {}

    public Beachbed(int aoId, int bbId, BigDecimal daily_price){
        setAdd_onId(aoId);
        this.beachbedId = bbId;
        this.daily_price = daily_price;
    }

    public int getBeachbedId() {
        return beachbedId;
    }

    public void setBeachbedId(int beachbedId) {
        this.beachbedId = beachbedId;
    }

    @Override
    public BigDecimal getPrice() {
        return this.daily_price;
    }

    @Override
    public String toString(){
        return "Lettino N°" + this.beachbedId + ", prezzo per giornata: " + this.daily_price +".";
    }
}
