package com.movie.booking.booking.controller;

import com.movie.booking.booking.dto.ApiResponse;
import com.movie.booking.booking.dto.BookingDtos.*;
import com.movie.booking.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Bookings", description = "Movie ticket booking lifecycle: create, confirm, and cancel")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;

    @Operation(summary = "Get booking by ID",
               description = "Returns a single booking by its UUID.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Booking retrieved", service.getById(id)));
    }

    @Operation(summary = "Get booking by reference code",
               description = "Returns a booking using the human-readable booking reference string.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @GetMapping("/ref/{ref}")
    public ResponseEntity<ApiResponse<BookingResponse>> getByRef(@PathVariable String ref) {
        return ResponseEntity.ok(ApiResponse.ok("Booking retrieved", service.getByRef(ref)));
    }

    @Operation(summary = "List my bookings",
               description = "Returns all bookings for the authenticated user. User ID is injected by the API gateway.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bookings retrieved")
    })
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok("Bookings retrieved", service.getByUser(userId)));
    }

    @Operation(summary = "List bookings for a show (Admin/Theatre Partner)",
               description = "Returns all bookings for a given show. Requires ADMIN or THEATRE_PARTNER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bookings retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/show/{showId}")
    @PreAuthorize("hasAnyRole('ADMIN','THEATRE_PARTNER')")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getByShow(@PathVariable UUID showId) {
        return ResponseEntity.ok(ApiResponse.ok("Bookings retrieved", service.getByShow(showId)));
    }

    @Operation(summary = "Create a booking",
               description = "Initiates a new booking (PENDING status). Seats are locked for payment. " +
                             "Call /confirm after successful payment.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Booking created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or seats unavailable")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> create(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateBookingRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Booking created", service.create(userId, req)));
    }

    @Operation(summary = "Confirm a booking",
               description = "Moves the booking from PENDING to CONFIRMED once payment succeeds. " +
                             "Requires the booking ID and the associated payment ID.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking confirmed"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Payment not associated or booking already confirmed")
    })
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<BookingResponse>> confirm(
            @Valid @RequestBody ConfirmBookingRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Booking confirmed", service.confirm(req)));
    }

    @Operation(summary = "Cancel a booking",
               description = "Cancels a booking and releases the reserved seats. Only the owner can cancel.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking cancelled"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Not the booking owner"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancel(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok("Booking cancelled", service.cancel(id, userId)));
    }
}
