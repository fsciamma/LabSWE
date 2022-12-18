package model;

import java.math.BigDecimal;

public class Gazebo extends Asset {

    private int gazeboId;


    public Gazebo(){
    }

    public Gazebo(int assetId, int gazeboId, BigDecimal daily_price) {
        super(assetId, daily_price);
        this.gazeboId = gazeboId;
    }

    public int getGazeboId() {
        return gazeboId;
    }

    public void setGazeboId(int gazeboId) {
        this.gazeboId = gazeboId;
    }

    @Override
    public String toString(){
        return "Gazebo N°" + this.gazeboId + ", prezzo per giornata: " + this.daily_price + "€.";
    }
}
