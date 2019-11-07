package com.mycompany.island.service;

import com.mycompany.island.exception.ReservationNotPossibleException;
import com.mycompany.island.model.AvailableReservation;
import com.mycompany.island.model.Reservation;
import com.mycompany.island.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * Handles multiples reservationsand check the available dates
 */
@Service
public class ReservationService {

	private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);


	public ReservationService(@Autowired ReservationRepository reservationRepository,
							  @Autowired DateValidationService dateValidationService) {
		super();
		this.reservationRepository = reservationRepository;
		this.dateValidationService = dateValidationService;
	}

	private ReservationRepository reservationRepository;
	private DateValidationService dateValidationService;


	ExecutorService executorService = Executors.newFixedThreadPool(10);

	/**
	 * Where there are more than 10 concurrent calls, the call will be put 
	 * in the executorService thread queue
	 * @param reservation
	 */
	public Reservation asynchronousTryReservation(Reservation reservation, boolean isUpdate) throws ReservationNotPossibleException{
		Future<Reservation> resultReservation =executorService.submit(() -> {
			return tryReservation(reservation,isUpdate);
		});

		//Intents 10 times before return an Id as result.
		for(int intents=0; intents<10 &&
				!resultReservation.isDone()
				&& !resultReservation.isCancelled();intents ++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


		try {
			return 	resultReservation.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			throw new ReservationNotPossibleException(e.getMessage());
		}
		return new Reservation();
	}

	private Reservation tryReservation(Reservation reservation, boolean isUpdate) throws ReservationNotPossibleException{
		dateValidationService.validateReservationDates(reservation);
		if(!reservationRepository.existsReservationBetween(reservation.getReservedFrom(),reservation.getReservedTo())){
			reservationRepository.save(reservation);
			return reservation;
		}else //is an update for a reservation
			if(isUpdate){
			reservationRepository.save(reservation);
			return reservation;
		}
		else{
			throw new ReservationNotPossibleException("there is an previous reservation");
		}
	}




	public List<AvailableReservation> findAllAvailableReservation(Date fromDate, Date toDate) {
		List<Reservation> reservations = reservationRepository.findAllBetween(fromDate,toDate);
		List<AvailableReservation> availableReservationList = new ArrayList<AvailableReservation>();

		//Cause we use  reservationRepository.existsReservationBetween method
		//before save an instance
		//Shouldn't be overlapping reservations
		Collections.sort(reservations, new Comparator<Reservation>() {
			@Override
			public int compare(Reservation r1, Reservation r2) {
				if(r1.getReservedFrom().after(r2.getReservedFrom())){
					return 1;
				}
				else{
					return -1;
				}

			}
		});

		AvailableReservation availableReservation = new AvailableReservation(fromDate,toDate);

		if(reservations.size()>1){
			Reservation lastReservation = reservations.get(0);
			Reservation currentReservation;
			int reservationIt =1;
			if(fromDate.after(lastReservation.getReservedFrom())){
				currentReservation = reservations.get(reservationIt);
				availableReservation.setAvailableFrom(lastReservation.getReservedTo());
				availableReservation.setAvailableTo(currentReservation.getReservedFrom());
				availableReservationList.add(availableReservation);
				reservationIt++;
				lastReservation = currentReservation;
			}
			else if(fromDate.before(lastReservation.getReservedFrom())){
				availableReservation.setAvailableTo(lastReservation.getReservedFrom());
				availableReservationList.add(availableReservation);
			}
			for (;reservationIt<reservations.size();reservationIt++){
				currentReservation = reservations.get(reservationIt);
				if(dateValidationService.getDiffInDays(lastReservation,currentReservation)>=1){
					availableReservationList.add(new AvailableReservation(lastReservation.getReservedTo(),currentReservation.getReservedFrom()));
				}
				lastReservation = currentReservation;
			}

			if(lastReservation.getReservedTo().before(toDate)){
				availableReservation = new AvailableReservation(lastReservation.getReservedTo(),toDate);
				availableReservationList.add(availableReservation);
			}
		}else if(reservations.size()==1){

			Reservation reservation = reservations.get(0);

			if(fromDate.before(reservation.getReservedFrom())){
				availableReservation.setAvailableTo(reservation.getReservedFrom());
				availableReservationList.add(availableReservation);

				if(reservation.getReservedTo().before(toDate)){
					availableReservation = new AvailableReservation(reservation.getReservedTo(),toDate);
					availableReservationList.add(availableReservation);
				}

			}else 	if(reservation.getReservedTo().before(toDate)){
				availableReservation.setAvailableFrom(reservation.getReservedTo());
				availableReservationList.add(availableReservation);
			}

		}else {
			availableReservationList.add(availableReservation);
		}


		return availableReservationList;
	}
}
