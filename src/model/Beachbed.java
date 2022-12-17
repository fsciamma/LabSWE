package model;

import java.math.BigDecimal;

public class Beachbed extends AddOn{

    private int beachbedId;

    public Beachbed() {
        super();
    }

    public Beachbed(int aoId, int bbId, BigDecimal daily_price){
        super(aoId, daily_price);
        this.beachbedId = bbId;
    }

    public int getBeachbedId() {
        return beachbedId;
    }

    public void setBeachbedId(int beachbedId) {
        this.beachbedId = beachbedId;
    }

    @Override
    public String toString(){
        return "Lettino N°" + this.beachbedId + ", prezzo per giornata: " + getPrice() +".";
    }
}
