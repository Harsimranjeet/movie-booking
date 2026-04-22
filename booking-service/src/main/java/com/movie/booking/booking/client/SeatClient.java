package com.movie.booking.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "seat-service")
public interface SeatClient {

    @PostMapping("/api/v1/seats/reserve")
    void reserve(@RequestBody Map<String, Object> body);

    @PostMapping("/api/v1/seats/confirm")
    void confirm(@RequestParam("bookingId") String bookingId);

    @PostMapping("/api/v1/seats/release")
    void release(@RequestParam("id") String bookingId);
}
