package model;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class ReservedAddOn {

    private AddOn addon;

    private LocalDate start_date;

    private LocalDate end_date;

    public ReservedAddOn(){}

    public ReservedAddOn(AddOn a, LocalDate start_date, LocalDate end_date){
        this.addon = a;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    public AddOn getAddon() {
        return addon;
    }

    public BigDecimal getPrice() {
        return addon.getPrice().multiply(BigDecimal.valueOf(DAYS.between(start_date, end_date)));
    }
}
