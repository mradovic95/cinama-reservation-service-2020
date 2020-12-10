package com.raf.reservationservicevezbe.service;

import com.raf.reservationservicevezbe.dto.ReservationCreateDto;

public interface ReservationService {

    void addReservation(ReservationCreateDto reservationCreateDto);
}
