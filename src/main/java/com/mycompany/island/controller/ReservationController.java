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
import java.util.*;

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
    public Reservation createReservation(@Valid @RequestBody Reservation reservation) throws ReservationNotPossibleException {

        return reservationService.asynchronousTryReservation(reservation.datesToCheckInAndCheckOutFormat(),false);
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
        newReservation.copyValuesForUpdate(reservation);
        reservationService.asynchronousTryReservation(newReservation.datesToCheckInAndCheckOutFormat(),true);
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
     * Get all reservations count.
     */
    @ApiOperation(value = "Get all reservation list size", responseContainer = "Integer")
    @GetMapping("/reservations/count")
    public Integer getAllReservationsCount() {
        return reservationRepository.findAll().size();
    }

    /**
     * Get all available reservations.
     */
    @ApiOperation(value = "Get all available reservation availables  list.", responseContainer = "List<AvailableReservation>")
    @GetMapping("/reservations/availables")
    public List<AvailableReservation> getAllAvailableReservations(@RequestParam(name = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
                                                                  @RequestParam(name = "to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) throws ReservationNotPossibleException {
        //By default is one month
        if (fromDate == null || toDate == null) {
            fromDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(fromDate);
            cal.add(Calendar.DATE, 30);
            toDate = cal.getTime();
        }

        if (toDate.before(fromDate)) {
            throw new ReservationNotPossibleException("invalid dates");
        }
        return reservationService.findAllAvailableReservation(fromDate, toDate);
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


    /**
     * Delete a reservation
     *
     * @param reservationId the reservation id
     * @return the map
     * @throws Exception the exception
     */
    @DeleteMapping("/reservations/{reservationId}")
    public Map<String, Boolean> deleteReservation(@PathVariable(value = "reservationId") Long reservationId) throws Exception {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotPossibleException("Reservation not found on :: " + reservationId));

        reservationRepository.delete(reservation);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    /**
     * Delete allthe reservations
     *
     * @return the map
     * @throws Exception the exception
     */
    @DeleteMapping("/reservations/all")
    public Map<String, Boolean> deleteReservations() throws Exception {
        reservationRepository.deleteAll();
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

}
