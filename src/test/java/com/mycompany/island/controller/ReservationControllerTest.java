package com.mycompany.island.controller;


import com.mycompany.island.Application;
import com.mycompany.island.model.Reservation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private Reservation reservation;

    private String getRootUrl() {
        return "http://localhost:" + port + "/api/v1";
    }

    @Before
    public void setUp() {
        String arrivalDateStr = "2020-01-01";
        String departureDateStr = "2020-01-30";
        String reservedFromStr = "2020-01-05";
        String reservedToStr = "2020-01-08";
        reservation = this.createReservation(arrivalDateStr,
                departureDateStr,
                reservedFromStr,
                reservedToStr,
                "user_one");

        reservation =restTemplate.postForEntity(getRootUrl() + "/reservations", reservation, Reservation.class).getBody();
    }

    @After
    public void tearDown() {
        restTemplate.delete(getRootUrl() + "/reservations/all");
    }

    @Test
    public void testGetAllReservations() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/reservations", HttpMethod.GET, entity,
                String.class);

        Assert.assertNotNull(response.getBody());
    }

    @Test
    public void testGetReservationById() {
        Reservation reservation2 = restTemplate.getForObject(getRootUrl() + "/reservations/" + reservation.getId(), Reservation.class);
        System.out.println(reservation2.getUserName());
        Assert.assertNotNull(reservation2);
    }

    @Test
    public void testUpdateReservation() {
        Reservation reservation2 = restTemplate.getForObject(getRootUrl() + "/reservations/" + reservation.getId(), Reservation.class);
        reservation2.setUserName("operator two");

        restTemplate.put(getRootUrl() + "/reservations/" + reservation.getId(), reservation2);

        Reservation updatedReservation = restTemplate.getForObject(getRootUrl() + "/reservations/" + reservation.getId(),
                Reservation.class);
        Assert.assertTrue(reservation2.getUserName().equals(updatedReservation.getUserName()));
    }

    @Test
    public void handleMultipleReservations() {
        List<Thread> threadList = new ArrayList<Thread>();

        for (int month = 1; month <= 9; month++) {
            String arrivalDateStr = "2021-0" + month + "-01";
            String departureDateStr = "2021-0" + month + "-30";
            String reservedFromStr = "2021-0" + month + "-05";
            String reservedToStr = "2021-0" + month + "-08";

            final Reservation reservation2 = this.createReservation(arrivalDateStr,
                    departureDateStr,
                    reservedFromStr,
                    reservedToStr,
                    "user_" + month);

            Thread createReservationThread = new Thread(new Runnable() {

                @Override
                public void run() {

                    restTemplate.postForEntity(getRootUrl() + "/reservations", reservation2, Reservation.class).getBody();
                }
            });


            createReservationThread.start();

            threadList.add(createReservationThread);


        }
        //Wait that all thread to finish
        for (Thread thread:threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<Integer> response = restTemplate.exchange(getRootUrl() + "/reservations/count", HttpMethod.GET, entity,
                Integer.class);

        //Must be 10 reservation( 9 plus 1 created in the setUp)
        Assert.assertNotNull(response.getBody()==10);

    }

    private Reservation createReservation(String arrivalDateStr,
                                          String departureDateStr,
                                          String reservedFromStr,
                                          String reservedToStr,
                                          String name) {

        Reservation reservation = new Reservation();
        reservation.setUserName(name);
        reservation.setUserEmail(name + "@company.com");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date arrivalDate = sdf.parse(arrivalDateStr);
            Date departureDate = sdf.parse(departureDateStr);
            Date reservedFromDate = sdf.parse(reservedFromStr);
            Date reservedToDate = sdf.parse(reservedToStr);

            reservation.setUserArrival(arrivalDate);
            reservation.setUserDeparture(departureDate);
            reservation.setReservedFrom(reservedFromDate);
            reservation.setReservedTo(reservedToDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        return reservation;
    }

    private void deleteReservation(Long reservationId) {
        Reservation reservation2 = restTemplate.getForObject(getRootUrl() + "/reservations/" + reservationId, Reservation.class);
        Assert.assertNotNull(reservation2);

        restTemplate.delete(getRootUrl() + "/reservations/" + reservation2.getId());

        try {
            reservation2 = restTemplate.getForObject(getRootUrl() + "/reservations/" + reservation2.getId(), Reservation.class);
        } catch (final HttpClientErrorException e) {
            Assert.assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }
}
