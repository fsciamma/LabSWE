package model;

import java.math.BigDecimal;

public class Umbrella extends Asset {

    private int umbrellaId;


    public Umbrella() {
        super();
    }

    public Umbrella(int assetId, int umbrellaId, BigDecimal daily_price) {
        super(assetId, daily_price);
        this.umbrellaId = umbrellaId;
    }

    public int getUmbrellaId() {
        return umbrellaId;
    }
    public void setUmbrellaId(int umbrellaId) {
        this.umbrellaId = umbrellaId;
    }

    @Override
    public String toString(){
        return "Ombrellone #" + this.umbrellaId + ", prezzo per giornata: " + this.daily_price + ".";
    }
}
