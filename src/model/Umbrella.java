package model;

public class Umbrella {

    private int umbrellaId;
    private String umbrellaType;
    private float daily_price;


    public int getUmbrellaId() {
        return umbrellaId;
    }
    public void setUmbrellaId(int umbrellaId) {
        this.umbrellaId = umbrellaId;
    }

    public void setValues(int tipoOmbrellone){
        this.umbrellaType = UmbrellaType.getInstance().getUTypeMap().get(tipoOmbrellone).getTypeName();
        this.daily_price = UmbrellaType.getInstance().getUTypeMap().get(tipoOmbrellone).getTypePrice();
    }
}