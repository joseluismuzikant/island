package com.mycompany.island.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.Date;


public class AvailableReservation {

    private static final Logger logger = LoggerFactory.getLogger(AvailableReservation.class);

    public AvailableReservation() {
    }

    public AvailableReservation(@NotNull(message = "Please provide an reserved from date") Date availableFrom,
                                @NotNull(message = "Please provide an reserved to date") Date availableTo) {
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
    }

    private Date availableFrom;
    private Date availableTo;

    public Date getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(Date availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Date getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(Date availableTo) {
        this.availableTo = availableTo;
    }

}
