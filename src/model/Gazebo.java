package model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Gazebo extends Asset {

    private int gazeboId;

    private BigDecimal daily_price;

    public Gazebo(){
    }

    public Gazebo(int resId, int gazeboId, BigDecimal daily_price) {
        setResId(resId);
        this.gazeboId = gazeboId;
        this.daily_price = daily_price;
        this.add_ons = new ArrayList<>();
    }

    public int getGazeboId() {
        return gazeboId;
    }

    public void setGazeboId(int gazeboId) {
        this.gazeboId = gazeboId;
    }

    @Override
    public BigDecimal getPrice() {
        return this.daily_price;
    }

    @Override
    public String toString(){
        return "Gazebo N°" + this.gazeboId + ", prezzo per giornata: " + this.daily_price + ".";
    }
}
