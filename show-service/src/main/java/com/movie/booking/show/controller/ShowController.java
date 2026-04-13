package com.movie.booking.show.controller;

import com.movie.booking.show.dto.ApiResponse;
import com.movie.booking.show.dto.ShowDtos.*;
import com.movie.booking.show.service.ShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Shows", description = "Movie show schedules: browse, create, update, and cancel")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService service;

    @Operation(summary = "Get show by ID",
               description = "Returns details of a single show.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Show retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Show not found")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<ShowResponse>> getById(@RequestParam("id") String id) {
        return ResponseEntity.ok(ApiResponse.ok("Show retrieved", service.getById(id)));
    }

    @Operation(summary = "List shows by movie and date",
               description = "Returns all shows for a given movie on a specific date (ISO-8601, e.g. 2024-07-15).")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shows retrieved")
    })
    @GetMapping("/movie")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getByMovie(
            @RequestParam("movieId") String movieId,
            @Parameter(description = "Show date in ISO-8601 format (yyyy-MM-dd)")
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<ShowResponse> shows = service.getByMovieAndDate(movieId, date);

        return ResponseEntity.ok(
                ApiResponse.ok("Shows retrieved successfully", shows)
        );
    }

    @Operation(summary = "List shows by theatre and date",
               description = "Returns all shows at a given theatre on a specific date.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shows retrieved")
    })
    @GetMapping("/theatre")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getByTheatre(
            @RequestParam("theatreId") String theatreId,
            @Parameter(description = "Show date in ISO-8601 format (yyyy-MM-dd)")
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<ShowResponse> shows = service.getByTheatreAndDate(theatreId, date);

        return ResponseEntity.ok(
                ApiResponse.ok("Shows retrieved successfully", shows)
        );
    }

    @Operation(summary = "Search shows",
               description = "Returns shows matching all of movie, theatre, and date.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shows retrieved")
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> search(
            @RequestParam(value = "movieId", required = false) String movieId,
            @RequestParam(value = "theatreId", required = false) String theatreId,
            @Parameter(description = "Show date in ISO-8601 format (yyyy-MM-dd)")
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<ShowResponse> shows =
                service.getByMovieTheatreDate(movieId, theatreId, date);

        return ResponseEntity.ok(
                ApiResponse.ok("Shows retrieved successfully", shows)
        );
    }

    @Operation(summary = "List shows by date",
               description = "Returns all shows on a specific date across all theatres.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shows retrieved")
    })
    @GetMapping("/date")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getByDate(
            @Parameter(description = "Show date in ISO-8601 format (yyyy-MM-dd)")
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<ShowResponse> shows = service.getByDate(date);

        return ResponseEntity.ok(
                ApiResponse.ok("Shows retrieved successfully", shows)
        );
    }

    @Operation(summary = "Create a show (Admin/Theatre Partner)",
               description = "Creates a new movie show. Requires ADMIN or THEATRE_PARTNER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Show created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','THEATRE_PARTNER')")
    public ResponseEntity<ApiResponse<ShowResponse>> create(@Valid @RequestBody CreateShowRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Show created", service.create(req)));
    }

    @Operation(summary = "Update a show (Admin/Theatre Partner)",
               description = "Updates scheduling or pricing details of an existing show.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Show updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Show not found")
    })
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','THEATRE_PARTNER')")
    public ResponseEntity<ApiResponse<ShowResponse>> update(
            @RequestParam("id") String id,
            @RequestBody UpdateShowRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Show updated", service.update(id, req)));
    }

    @Operation(summary = "Cancel a show (Admin/Theatre Partner)",
               description = "Marks a show as CANCELLED. Requires ADMIN or THEATRE_PARTNER role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Show cancelled"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Show not found")
    })
    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN','THEATRE_PARTNER')")
    public ResponseEntity<ApiResponse<Void>> cancel(@RequestParam String id) {
        service.cancel(id);
        return ResponseEntity.ok(ApiResponse.ok("Show cancelled"));
    }
}
