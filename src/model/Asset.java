package model;

import java.math.BigDecimal;

public class Asset {
    protected int assetId;

    protected BigDecimal daily_price;

    protected Asset(){}

    protected Asset(int assetId, BigDecimal price){
        this.assetId = assetId;
        this.daily_price = price;
    }

    public int getResId() {
        return assetId;
    }

    public void setResId(int resId) {
        this.assetId = resId;
    }

    public BigDecimal getPrice(){
        return this.daily_price;
    }
}
