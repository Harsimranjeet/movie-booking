package com.movie.booking.theatre.controller;

import com.movie.booking.theatre.dto.ApiResponse;
import com.movie.booking.theatre.dto.TheatreDtos.*;
import com.movie.booking.theatre.service.TheatreService;
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

@Tag(name = "Theatres", description = "Theatre and screen management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/theatres")
@RequiredArgsConstructor
public class TheatreController {

    private final TheatreService service;

    @Operation(summary = "List all theatres", description = "Returns all active theatres.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Theatres retrieved")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TheatreResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("Theatres retrieved", service.getAll()));
    }

    @Operation(summary = "Get theatre by ID", description = "Returns a single theatre by its UUID.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Theatre retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Theatre not found")
    })
    @GetMapping(params = "id")
    public ResponseEntity<ApiResponse<TheatreResponse>> getById(@RequestParam("id") String id) {
        return ResponseEntity.ok(ApiResponse.ok("Theatre retrieved", service.getById(id)));
    }

    @Operation(summary = "List theatres by city", description = "Returns all theatres in the specified city.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Theatres retrieved")
    @GetMapping("/city")
    public ResponseEntity<ApiResponse<List<TheatreResponse>>> getByCity(@RequestParam("city") String city) {
        return ResponseEntity.ok(ApiResponse.ok("Theatres retrieved", service.getByCity(city)));
    }

    @Operation(summary = "Create a theatre (Admin/Theatre Partner)",
               description = "Registers a new theatre. Requires ADMIN or THEATRE_PARTNER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Theatre created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','THEATRE_PARTNER')")
    public ResponseEntity<ApiResponse<TheatreResponse>> create(@Valid @RequestBody CreateTheatreRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Theatre created", service.create(req)));
    }

    @Operation(summary = "Update a theatre (Admin/Theatre Partner)",
               description = "Updates theatre details. Requires ADMIN or THEATRE_PARTNER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Theatre updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Theatre not found")
    })
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','THEATRE_PARTNER')")
    public ResponseEntity<ApiResponse<TheatreResponse>> update(
            @RequestParam("id") String id, @RequestBody UpdateTheatreRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Theatre updated", service.update(id, req)));
    }

    @Operation(summary = "Delete a theatre (Admin)", description = "Removes a theatre. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Theatre deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Theatre not found")
    })
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam ("id") String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Theatre deleted"));
    }

    @Operation(summary = "List screens for a theatre",
               description = "Returns all screens configured for the specified theatre.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Screens retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Theatre not found")
    })
    @GetMapping("screens")
    public ResponseEntity<ApiResponse<List<ScreenResponse>>> getScreens(@RequestParam("theatreId") String theatreId) {
        return ResponseEntity.ok(ApiResponse.ok("Screens retrieved", service.getScreens(theatreId)));
    }

    @Operation(summary = "Add a screen to a theatre (Admin/Theatre Partner)",
               description = "Creates a new screen inside a theatre. Requires ADMIN or THEATRE_PARTNER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Screen added"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Theatre not found")
    })
    @PostMapping("/screens")
    @PreAuthorize("hasAnyRole('ADMIN','THEATRE_PARTNER')")
    public ResponseEntity<ApiResponse<ScreenResponse>> addScreen(
            @RequestParam("theatreId") String theatreId, @Valid @RequestBody CreateScreenRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Screen added", service.addScreen(theatreId, req)));
    }

    @Operation(summary = "Delete a screen (Admin/Theatre Partner)",
               description = "Removes a screen by its ID. Requires ADMIN or THEATRE_PARTNER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Screen deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Screen not found")
    })
    @DeleteMapping("/screens")
    @PreAuthorize("hasAnyRole('ADMIN','THEATRE_PARTNER')")
    public ResponseEntity<ApiResponse<Void>> deleteScreen(@RequestParam ("id") String id) {
        service.deleteScreen(id);
        return ResponseEntity.ok(ApiResponse.ok("Screen deleted"));
    }
}
