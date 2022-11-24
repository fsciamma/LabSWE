package model;

import java.math.BigDecimal;

public class Chair extends AddOn{

    private int chairId;

    private BigDecimal daily_price;

    public Chair() {
    }

    @Override
    public BigDecimal getPrice() {
        return this.daily_price;
    }
}
