package model;

import java.math.BigDecimal;

public class Booth extends AddOn{

    private int bothId;

    private BigDecimal daily_price;

    public Booth() {
    }


    @Override
    public BigDecimal getPrice() {
        return this.daily_price;
    }
}
