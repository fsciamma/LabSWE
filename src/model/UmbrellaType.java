package model;

import java.util.HashMap;
import java.util.Map;

public class UmbrellaType {
    private static UmbrellaType INSTANCE;
    private Map<Integer, TypeDetails> uTypeMap;

    private UmbrellaType() {
        this.uTypeMap = new HashMap<>();
    }

    public static UmbrellaType getInstance(){
        if(INSTANCE == null){
            INSTANCE = new UmbrellaType();
        }
        return INSTANCE;
    }

    public Map<Integer, TypeDetails> getUTypeMap() {
        return uTypeMap;
    }
}
