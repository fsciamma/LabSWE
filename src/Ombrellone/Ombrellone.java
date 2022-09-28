package Ombrellone;

public class Ombrellone {

    private String idOmbrellone;
    private boolean free;

    public Ombrellone(final String idOmbrellone){
        this.idOmbrellone = idOmbrellone;
    }

    public String getIdOmbrellone(){
        return this.idOmbrellone;
    }

    public boolean isFree() {
        return this.free;
    }
}
