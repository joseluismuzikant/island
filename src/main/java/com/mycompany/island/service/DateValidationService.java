package com.mycompany.island.service;

import com.mycompany.island.exception.ReservationNotPossibleException;
import com.mycompany.island.model.Reservation;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class DateValidationService {

    public void validateReservationDates(Reservation reservation) throws ReservationNotPossibleException {
        boolean isValid = true;
        String message = "";

        if(!reservation.getReservedFrom().before(reservation.getReservedTo())){
            isValid = false;
            message = "from reservation date must be before to reservation date";
        }

        if(!reservation.getReservedFrom().after(new Date())){
            isValid = false;
            message = "from reservation date must be after today";
        }

        if(!reservation.getReservedFrom().after(reservation.getUserArrival())){
            isValid = false;
            message = "from reservation date must be after the user arrival";
        }

        if(!reservation.getUserDeparture().after(reservation.getUserArrival())){
            isValid = false;
            message = "user departure date  must be after the user arrival";
        }

        if(!reservation.getUserDeparture().after(reservation.getReservedTo())){
            isValid = false;
            message = "user departure date  must be after the user reservation";
        }


        long diffInDays = getDiffInDays(reservation);
        long diffInMillies;

        //Validate if the reservation is maximun for three days
        if(diffInDays >3){
            isValid = false;
            message = "The maximun reservation period is three days";
        }
        //Validate if the reservation is at least one day
        //Is not in the requirement but it makes sense
        if(diffInDays <1){
            isValid = false;
            message = "The reservation must be at least one day";
        }

        //Validate if the reservation is  1 day ahead of arrival
        diffInMillies = Math.abs(reservation.getReservedFrom().getTime() - reservation.getUserArrival().getTime());
        diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if(diffInDays <1){
            isValid = false;
            message = "The reservation must be one day ahead of arrival";
        }
        if(diffInDays >301){
            isValid = false;
            message = "The reservation must be one month in advance of the arrival";
        }
        if(!isValid){
            throw new ReservationNotPossibleException(message);
        }
    }

    public long getDiffInDays(Reservation reservation) {
        long diffInMillies = Math.abs(reservation.getReservedTo().getTime() - reservation.getReservedFrom().getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public long getDiffInDays(Reservation r1,Reservation r2) {
        long diffInMillies = Math.abs(r1.getReservedTo().getTime() - r2.getReservedFrom().getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
