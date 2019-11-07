package com.mycompany.island.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;

@Entity
public class Reservation {

    private static final Logger logger = LoggerFactory.getLogger(Reservation.class);

    public Reservation() {
    }


    public Reservation(@NotNull(message = "Please provide a user name") String userName,
                       @NotNull(message = "Please provide a user email") String userEmail,
                       @NotNull(message = "Please provide an arrival date") Date userArrival,
                       @NotNull(message = "Please provide an reserved from date") Date userDeparture,
                       @NotNull(message = "Please provide an reserved from date") Date reservedFrom,
                       @NotNull(message = "Please provide an reserved to date") Date reservedTo) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userArrival = userArrival;
        this.userDeparture = userDeparture;
        this.reservedFrom = reservedFrom;
        this.reservedTo = reservedTo;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Please provide a user name")
    @Column
    private String userName;

    @NotNull(message = "Please provide a user email")
    @Column
    private String userEmail;


    @NotNull(message = "Please provide an arrival date")
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date userArrival;

    @NotNull(message = "Please provide an departure date")
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date userDeparture;

    @Column
    @NotNull
    private Boolean cancelled = false;

    @NotNull(message = "Please provide an reserved from date")
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date reservedFrom;

    @NotNull(message = "Please provide an reserved to date")
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date reservedTo;


    public void copyValuesForUpdate(Reservation reservation) {
        this.cancelled = reservation.cancelled;
        this.userEmail = reservation.userEmail;
        this.userName = reservation.userName;
    }

    public Reservation datesToCheckInAndCheckOutFormat() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.reservedFrom);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        this.reservedFrom = cal.getTime();

        cal.setTime(this.reservedTo);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        this.reservedTo = cal.getTime();

        return this;

    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public java.util.Date getUserArrival() {
        return userArrival;
    }

    public java.util.Date getUserDeparture() {
        return userDeparture;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public Boolean isCancelled() {
        return cancelled;
    }

    public java.util.Date getReservedFrom() {
        return reservedFrom;
    }

    public java.util.Date getReservedTo() {
        return reservedTo;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserArrival(Date userArrival) {
        this.userArrival = userArrival;
    }

    public void setUserDeparture(Date userDeparture) {
        this.userDeparture = userDeparture;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setReservedFrom(Date reservedFrom) {
        this.reservedFrom = reservedFrom;
    }

    public void setReservedTo(Date reservedTo) {
        this.reservedTo = reservedTo;
    }
}
