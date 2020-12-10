package com.raf.reservationservicevezbe.service.impl;

import com.raf.reservationservicevezbe.client.movieservice.dto.ProjectionDto;
import com.raf.reservationservicevezbe.client.userservice.dto.DiscountDto;
import com.raf.reservationservicevezbe.domain.Reservation;
import com.raf.reservationservicevezbe.dto.ReservationCreateDto;
import com.raf.reservationservicevezbe.exception.NotFoundException;
import com.raf.reservationservicevezbe.repository.ReservationRepository;
import com.raf.reservationservicevezbe.service.ReservationService;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class ReservationServiceImpl implements ReservationService {

    private ReservationRepository reservationRepository;
    private RestTemplate movieServiceRestTemplate;
    private RestTemplate userServiceRestTemplate;

    public ReservationServiceImpl(ReservationRepository reservationRepository, RestTemplate movieServiceRestTemplate, RestTemplate userServiceRestTemplate) {
        this.reservationRepository = reservationRepository;
        this.movieServiceRestTemplate = movieServiceRestTemplate;
        this.userServiceRestTemplate = userServiceRestTemplate;
    }

    @Override
    public void addReservation(ReservationCreateDto reservationCreateDto) {
        //get projection from movie service
        ResponseEntity<ProjectionDto> projectionDtoResponseEntity = null;
        try {
            projectionDtoResponseEntity = movieServiceRestTemplate.exchange("/projection/"
                    + reservationCreateDto.getProjectionId(), HttpMethod.GET, null, ProjectionDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException(String.format("Projection with id: %d not found.", reservationCreateDto.getProjectionId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //get discount from user service
        ResponseEntity<DiscountDto> discountDtoResponseEntity = userServiceRestTemplate.exchange("/user/" +
                reservationCreateDto.getUserId() + "/discount", HttpMethod.GET, null, DiscountDto.class);
        //calculate price
        BigDecimal price = projectionDtoResponseEntity.getBody().getPrice().divide(BigDecimal.valueOf(100))
                .multiply(BigDecimal.valueOf(100 - discountDtoResponseEntity.getBody().getDiscount()));
        //save reservation
        Reservation reservation = new Reservation(reservationCreateDto.getUserId(), reservationCreateDto.getProjectionId(), price);
        reservationRepository.save(reservation);
    }
}
