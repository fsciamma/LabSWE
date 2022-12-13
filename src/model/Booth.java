package model;

import java.math.BigDecimal;

public class Booth extends AddOn{

    private int boothId;

    private BigDecimal daily_price;

    public Booth() {
    }

    public Booth(int aoId, int boothId, BigDecimal daily_price) {
        setAdd_onId(aoId);
        this.boothId = boothId;
        this.daily_price = daily_price;
    }

    public int getBoothId() {
        return boothId;
    }

    public void setBoothId(int boothId) {
        this.boothId = boothId;
    }

    @Override
    public BigDecimal getPrice() {
        return this.daily_price;
    }

    @Override
    public String toString(){
        return "Lettino N°" + this.boothId + ", prezzo per giornata: " + this.daily_price +".";
    }
}
