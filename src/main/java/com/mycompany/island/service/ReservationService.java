package com.mycompany.island.service;

import com.mycompany.island.exception.ReservationNotPossibleException;
import com.mycompany.island.model.AvailableReservation;
import com.mycompany.island.model.Reservation;
import com.mycompany.island.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * Handles multiples calls among the available employees
 */
@Service
public class ReservationService {

	private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

	private static final Long INVALID_ID = -1l;

	public ReservationService(@Autowired ReservationRepository reservationRepository,
							  @Autowired DatesValidationService datesValidationService) {
		super();
		this.reservationRepository = reservationRepository;
		this.datesValidationService = datesValidationService;
	}

	private ReservationRepository reservationRepository;
	private DatesValidationService datesValidationService;


	ExecutorService executorService = Executors.newFixedThreadPool(10);

	/**
	 * Where there are more than 10 concurrent calls, the call will be put 
	 * in the executorService thread queue
	 * @param reservation
	 */
	public Long asynchronousTryReservation(Reservation reservation) {
		Future<Long> resultReservation =executorService.submit(() -> {
			try {
				return tryReservation(reservation);
			} catch (ReservationNotPossibleException e) {
				logger.error(e.getMessage());
				return INVALID_ID;
			}
		});

		while(!resultReservation.isDone()) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


		try {
			return 	resultReservation.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return INVALID_ID;
	}

	private Long tryReservation(Reservation reservation) throws ReservationNotPossibleException{
		datesValidationService.validateReservationDates(reservation);
		if(reservation.getCancelled()
				|| !reservationRepository.existsReservationBetween(reservation.getReservedFrom(),reservation.getReservedTo())){
			reservationRepository.save(reservation);
			return reservation.getId();
		}
		else{
			throw new ReservationNotPossibleException("there is an previous reservation");
		}
	}




	public List<AvailableReservation> findAllAvailableReservation(Date fromDate, Date toDate) {
		return null;
	}
}
