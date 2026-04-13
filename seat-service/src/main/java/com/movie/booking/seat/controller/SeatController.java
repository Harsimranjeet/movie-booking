package com.movie.booking.seat.controller;

import com.movie.booking.seat.dto.ApiResponse;
import com.movie.booking.seat.model.Seat;
import com.movie.booking.seat.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Seats", description = "Seat inventory, availability, reservations, and releases")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService service;

    @Operation(summary = "List all seats for a show",
               description = "Returns every seat (of any status) for the specified show.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Seats retrieved")
    })
    @GetMapping("/show")
    public ResponseEntity<ApiResponse<List<Seat>>> getByShow(@RequestParam("id") String id) {
        return ResponseEntity.ok(ApiResponse.ok("Seats retrieved", service.getByShow(id)));
    }

    @Operation(summary = "List available seats for a show",
               description = "Returns only seats that are currently available (not reserved or sold).")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Available seats retrieved")
    })
    @GetMapping("/show/available")
    public ResponseEntity<ApiResponse<List<Seat>>> getAvailable(@RequestParam("id") String id) {
        return ResponseEntity.ok(ApiResponse.ok("Available seats", service.getAvailableByShow(id)));
    }

    @Operation(summary = "List seats by show and category",
               description = "Filters seats for a show by category (e.g. STANDARD, PREMIUM, VIP).")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Seats retrieved")
    })
    @GetMapping("/show/category")
    public ResponseEntity<ApiResponse<List<Seat>>> getByCategory(
            @RequestParam("showId") String showId,
            @RequestParam("category") String category) {
        return ResponseEntity.ok(ApiResponse.ok("Seats retrieved", service.getByShowAndCategory(showId, category)));
    }

    @Operation(summary = "Bulk create seats for a show",
               description = "Creates multiple seats for a show in a single request. Used when a show is set up.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Seats created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/show/bulk")
    public ResponseEntity<ApiResponse<List<Seat>>> createBulk(
            @RequestParam("showId") String showId,
            @RequestParam("screenId") String screenId,
            @RequestBody List<SeatService.SeatCreateRequest> requests) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Seats created", service.createSeatsForShow(showId, screenId, requests)));
    }

    @Operation(summary = "Reserve seats",
               description = "Locks the specified seats for the given booking ID. " +
                             "Body: `{ \"seatIds\": [\"uuid\", ...], \"bookingId\": \"uuid\", \"lockMinutes\": 10 }`.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Seats reserved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Seats already taken or invalid")
    })
    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<List<Seat>>> reserve(@RequestBody Map<String, Object> body) {
        List<UUID> seatIds = ((List<String>) body.get("seatIds")).stream().map(UUID::fromString).toList();
        UUID bookingId = UUID.fromString((String) body.get("bookingId"));
        int lockMins  = body.containsKey("lockMinutes") ? (int) body.get("lockMinutes") : 10;
        return ResponseEntity.ok(ApiResponse.ok("Seats reserved", service.reserveSeats(seatIds, bookingId, lockMins)));
    }

    @Operation(summary = "Confirm seats for a booking",
               description = "Marks reserved seats as SOLD once the booking is confirmed.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Seats confirmed"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No reserved seats found for booking")
    })
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<Void>> confirm(@RequestParam("bookingId") String bookingId) {
        service.confirmSeats(bookingId);
        return ResponseEntity.ok(ApiResponse.ok("Seats confirmed"));
    }

    @Operation(summary = "Release seats for a booking",
               description = "Returns reserved seats back to AVAILABLE status when a booking is cancelled.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Seats released"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No reserved seats found for booking")
    })
    @PostMapping("/release")
    public ResponseEntity<ApiResponse<Void>> release(@RequestParam("id") String id) {
        service.releaseSeats(id);
        return ResponseEntity.ok(ApiResponse.ok("Seats released"));
    }
}
