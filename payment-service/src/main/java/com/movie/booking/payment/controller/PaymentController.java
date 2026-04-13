package com.movie.booking.payment.controller;

import com.movie.booking.payment.dto.ApiResponse;
import com.movie.booking.payment.dto.PaymentDtos.*;
import com.movie.booking.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Payments", description = "Payment initiation, processing, and refunds")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @Operation(summary = "Get payment by ID",
               description = "Returns a single payment record by its UUID.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> getById(@RequestParam("id") String id) {
        return ResponseEntity.ok(ApiResponse.ok("Payment retrieved", service.getById(id)));
    }

    @Operation(summary = "Get payment by booking ID",
               description = "Returns the payment associated with a specific booking.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No payment found for booking")
    })
    @GetMapping("/booking")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByBooking(@RequestParam("id") String id) {
        return ResponseEntity.ok(ApiResponse.ok("Payment retrieved", service.getByBooking(id)));
    }

    @Operation(summary = "List payments by user",
               description = "Returns all payment records for a given user.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payments retrieved")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok("Payments retrieved", service.getByUser(userId)));
    }

    @Operation(summary = "Initiate a payment",
               description = "Creates a payment record in PENDING status for a booking. " +
                             "Call /process after collecting payment details from the user.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Payment initiated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or booking not found")
    })
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiate(@Valid @RequestBody InitiatePaymentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Payment initiated", service.initiate(req)));
    }

    @Operation(summary = "Process a payment",
               description = "Processes a pending payment using the provided payment details (card, UPI, etc.). " +
                             "On success, the payment moves to SUCCESS and the booking is confirmed.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment processed"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Payment already processed or invalid details"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<PaymentResponse>> process(@Valid @RequestBody ProcessPaymentRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Payment processed", service.process(req)));
    }

    @Operation(summary = "Refund a payment",
               description = "Initiates a refund for a previously successful payment.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Refund processed"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Payment not refundable or already refunded"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PostMapping("/refund")
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(@Valid @RequestBody RefundRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Refund processed", service.refund(req)));
    }
}
