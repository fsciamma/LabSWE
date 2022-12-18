package model;

import java.math.BigDecimal;

public class AddOn {
    protected int add_onId;

    protected BigDecimal price;

    public AddOn() {
    }

    public AddOn(int add_onId, BigDecimal price) {
        this.add_onId = add_onId;
        this.price = price;
    }

    public void setAdd_onId(int id){
        this.add_onId = id;
    }

    public int getAdd_onId() {
        return add_onId;
    }

    public BigDecimal getPrice(){
        return this.price;
    }
}
