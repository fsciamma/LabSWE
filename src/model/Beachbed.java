package model;

import java.math.BigDecimal;

public class Beachbed extends AddOn{

    private int beachbedId;

    private BigDecimal daily_price;

    public Beachbed() {}

    @Override
    public BigDecimal getPrice() {
        return this.daily_price;
    }
}
