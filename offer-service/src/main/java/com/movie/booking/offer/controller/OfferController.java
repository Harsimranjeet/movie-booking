package com.movie.booking.offer.controller;

import com.movie.booking.offer.dto.ApiResponse;
import com.movie.booking.offer.dto.OfferDtos.*;
import com.movie.booking.offer.service.OfferService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Offers", description = "Discount offers and promo code management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService service;

    @Operation(summary = "List active offers",
               description = "Returns all currently active and valid discount offers.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active offers retrieved")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<OfferResponse>>> getActive() {
        return ResponseEntity.ok(ApiResponse.ok("Active offers", service.getActive()));
    }

    @Operation(summary = "Get offer by ID",
               description = "Returns a single offer by its UUID.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Offer retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Offer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OfferResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Offer retrieved", service.getById(id)));
    }

    @Operation(summary = "Create an offer (Admin)",
               description = "Creates a new discount offer with a promo code. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Offer created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or duplicate code"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OfferResponse>> create(@Valid @RequestBody CreateOfferRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Offer created", service.create(req)));
    }

    @Operation(summary = "Apply an offer",
               description = "Validates and applies a promo code to a booking amount. " +
                             "Returns the original amount, discount amount, and final payable amount.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Offer applied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid, expired, or exhausted offer")
    })
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<DiscountResult>> apply(@Valid @RequestBody ApplyOfferRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Offer applied", service.apply(req)));
    }

    @Operation(summary = "Deactivate an offer (Admin)",
               description = "Marks an offer as inactive so it can no longer be applied. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Offer deactivated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Offer not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        service.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok("Offer deactivated"));
    }
}
