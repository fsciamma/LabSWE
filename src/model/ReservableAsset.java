package model;

import java.math.BigDecimal;

public class ReservableAsset {
    protected int resId;

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public BigDecimal getPrice(){
        return BigDecimal.ZERO;
    }
}
