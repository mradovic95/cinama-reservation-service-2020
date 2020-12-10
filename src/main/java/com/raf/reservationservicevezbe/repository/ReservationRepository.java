package com.raf.reservationservicevezbe.repository;

import com.raf.reservationservicevezbe.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByUserIdAndProjectionId(Long userId, Long projectionId);
}
