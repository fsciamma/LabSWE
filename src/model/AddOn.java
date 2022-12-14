package model;

import java.math.BigDecimal;

public class AddOn {
    protected int add_onId;

    public void setAdd_onId(int id){
        this.add_onId = id;
    }

    public int getAdd_onId() {
        return add_onId;
    }

    public BigDecimal getPrice(){
        return BigDecimal.ZERO;
    }
}
