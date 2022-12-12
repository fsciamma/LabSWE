package model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Gazebo extends Asset {

    private int gazeboId;

    private ArrayList<Integer> add_ons;

    private BigDecimal daily_price;

    public Gazebo(){
    }

    public Gazebo(int gazeboId, BigDecimal daily_price) {
        this.gazeboId = gazeboId;
        this.daily_price = daily_price;
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
        return "Ombrellone #" + this.gazeboId + ", prezzo per giornata: " + this.daily_price + ".";
    }
}