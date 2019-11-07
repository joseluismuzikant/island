package com.mycompany.island.repository;

import com.mycompany.island.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Reservation repository.
 *
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "select case when count(r)> 0 then true else false end from Reservation r where  r.cancelled = false AND (r.reservedFrom BETWEEN :fromDate AND :toDate) OR  (r.reservedTo BETWEEN :fromDate AND :toDate)")
    boolean existsReservationBetween(@Param("fromDate") Date fromDate, @Param("toDate")Date toDate);

    @Query(value = "select r from Reservation r where  r.cancelled = false   AND r.reservedFrom < :toDate  AND  r.reservedTo > :fromDate")
    List<Reservation> findAllBetween(@Param("fromDate") Date fromDate, @Param("toDate")Date toDate);
}
