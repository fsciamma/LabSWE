package model;

import java.math.BigDecimal;

public class ReservableAsset {
    private int resId;

    public int getResId() {
        return resId;
    }

    public BigDecimal getPrice(){
        return BigDecimal.ZERO;
    }
}
