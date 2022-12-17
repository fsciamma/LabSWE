package model;

import java.math.BigDecimal;

public class Booth extends AddOn{

    private int boothId;

    public Booth() {
        super();
    }

    public Booth(int aoId, int boothId, BigDecimal daily_price) {
        super(aoId, daily_price);
        this.boothId = boothId;
    }

    public int getBoothId() {
        return boothId;
    }

    public void setBoothId(int boothId) {
        this.boothId = boothId;
    }

    @Override
    public String toString(){
        return "Lettino N°" + this.boothId + ", prezzo per giornata: " + getPrice() +".";
    }
}
