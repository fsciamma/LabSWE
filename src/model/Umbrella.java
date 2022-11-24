package model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Umbrella extends ReservableAsset{

    private int umbrellaId;

    private ArrayList<Integer> add_ons;
    private BigDecimal daily_price;
    public Umbrella() {
    }

    public Umbrella(int umbrellaId, BigDecimal daily_price) {
        this.umbrellaId = umbrellaId;
        this.daily_price = daily_price;
    }

    public int getUmbrellaId() {
        return umbrellaId;
    }
    public void setUmbrellaId(int umbrellaId) {
        this.umbrellaId = umbrellaId;
    }

    @Override
    public BigDecimal getPrice() {
        return daily_price;
    }

    @Override
    public String toString(){
        return "Ombrellone #" + this.umbrellaId + ", prezzo per giornata: " + this.daily_price + ".";
    }
}
