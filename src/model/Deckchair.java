package model;

import java.math.BigDecimal;

public class Deckchair extends AddOn {

    private int deckchairId;

    private BigDecimal daily_price;

    public Deckchair() {
    }

    @Override
    public BigDecimal getPrice() {
        return this.daily_price;
    }
}
