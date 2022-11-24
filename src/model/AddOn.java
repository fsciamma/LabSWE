package model;

import java.math.BigDecimal;

public abstract class AddOn {
    private int add_onId;
    public BigDecimal getPrice(){
        return BigDecimal.ZERO;
    }
}
