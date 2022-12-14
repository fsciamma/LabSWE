package model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Asset {
    protected int resId;

    protected ArrayList<AddOn> add_ons;

    public void setAddOns(ArrayList<AddOn> a){
        this.add_ons.addAll(a);
    }

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
