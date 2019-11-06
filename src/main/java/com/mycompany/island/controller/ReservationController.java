package com.mycompany.island.controller;

import com.mycompany.island.exception.ReservationNotPossibleException;
import com.mycompany.island.model.AvailableReservation;
import com.mycompany.island.model.Reservation;
import com.mycompany.island.repository.ReservationRepository;
import com.mycompany.island.service.ReservationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * The type ReservationController controller.
 */
@Api(value = "/api/v1", description = "Api for make island reservations")
@RestController()
@RequestMapping("/api/v1")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * Create a reservation.
     */
    @PostMapping("/reservations")
    public Long createReservation(@Valid @RequestBody Reservation reservation) throws ReservationNotPossibleException {
        return reservationService.asynchronousTryReservation(reservation);
    }


    /**
     * Modify  a Reservation
     *
     * @param reservationId the reservation id
     * @param reservation   the reservation to modify
     * @return the reservation modified
     * @throws ReservationNotPossibleException if the reservation is not possible
     */
    @PutMapping("/reservations/{reservationId}")
    public ResponseEntity<Reservation> modifyReservation(@PathVariable(value = "reservationId") Long reservationId,
                                                         @Valid @RequestBody Reservation reservation) throws ReservationNotPossibleException {
        Reservation newReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotPossibleException("Reservation not found on :: " + reservationId));
        newReservation.copyValuesFrom(reservation);
        reservationService.asynchronousTryReservation(newReservation);
        return ResponseEntity.ok(newReservation);
    }


    /**
     * Get all reservations.
     */
    @ApiOperation(value = "Get all reservation list.", responseContainer = "List<Reservation>")
    @GetMapping("/reservations")
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Get all available reservations.
     */
    @ApiOperation(value = "Get all available reservation  list.", responseContainer = "List<AvailableReservation>")
    @GetMapping("/reservations/availables")
    public List<AvailableReservation> getAllAvailableReservations(@RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
																  @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate) {

        return reservationService.findAllAvailableReservation(fromDate,toDate);
    }

    /**
     * Gets a reservation by id.
     *
     * @param reservationId the reservation id
     * @return the reservation by id
     * @throws ReservationNotPossibleException the resource not found exception
     */
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable(value = "reservationId") Long reservationId) throws ReservationNotPossibleException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotPossibleException("Reservation not found on :: " + reservationId));
        return ResponseEntity.ok().body(reservation);
    }

}
