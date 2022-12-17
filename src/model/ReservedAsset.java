package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static java.time.temporal.ChronoUnit.DAYS;

public class ReservedAsset {
    private Asset asset;
    private ArrayList<ReservedAddOn> add_ons;

    private LocalDate start_date;

    private LocalDate end_date;

    public ReservedAsset() {
    }

    public ReservedAsset(Asset asset, LocalDate start_date, LocalDate end_date) {
        this.asset = asset;
        this.start_date = start_date;
        this.end_date = end_date;
        this.add_ons = new ArrayList<>();
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

    public BigDecimal getPrice(){
        BigDecimal asset_price = asset.getPrice().multiply(BigDecimal.valueOf(DAYS.between(start_date, end_date)));
        BigDecimal addon_price = BigDecimal.ZERO;
        for (ReservedAddOn r: add_ons) {
            addon_price = addon_price.add(r.getPrice().multiply(BigDecimal.valueOf(DAYS.between(r.getStart_date(), r.getEnd_date()))));
        }
        return asset_price.add(addon_price);
    }
}
