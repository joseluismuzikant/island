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

    @Query(value = "select case when count(r)> 0 then true else false end from Reservation r where  r.cancelled = 0 AND (reservedFrom BETWEEN :fromDate AND :toDate) OR  (reservedTo BETWEEN :fromDate AND :toDate)")
    public boolean existsReservationBetween(@Param("fromDate") Date fromDate, @Param("toDate")Date toDate);
}
