package com.movie.booking.movie.controller;

import com.movie.booking.movie.dto.ApiResponse;
import com.movie.booking.movie.dto.MovieDtos.*;
import com.movie.booking.movie.model.Genre;
import com.movie.booking.movie.model.Language;
import com.movie.booking.movie.service.GenreService;
import com.movie.booking.movie.service.LanguageService;
import com.movie.booking.movie.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Movies", description = "Movie catalogue management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final GenreService genreService;
    private final LanguageService languageService;


    @Operation(summary = "List all movies", description = "Returns all active movies in the catalogue.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Movies retrieved")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("Movies retrieved", movieService.getAll()));
    }

    @Operation(summary = "Get movie by ID", description = "Returns a single movie by its UUID.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Movie retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @GetMapping(params = "id")
    public ResponseEntity<ApiResponse<MovieResponse>> getById(@RequestParam("id") String id) {
        return ResponseEntity.ok(ApiResponse.ok("Movie retrieved", movieService.getById(id)));
    }

    @Operation(summary = "Search movies by title", description = "Case-insensitive partial match on the movie title.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> search(
            @Parameter(description = "Partial or full movie title") @RequestParam("title") String title) {
        return ResponseEntity.ok(ApiResponse.ok("Search results", movieService.search(title)));
    }

    @Operation(summary = "List movies by language", description = "Returns all movies available in the specified language.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Movies retrieved")
    @GetMapping(params = "language")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> byLanguage(@RequestParam("language") String language) {
        return ResponseEntity.ok(ApiResponse.ok("Movies retrieved", movieService.getByLanguage(language)));
    }

    @Operation(summary = "List movies by genre", description = "Returns all movies belonging to the specified genre.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Movies retrieved")
    @GetMapping(params = "genre")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> byGenre(@RequestParam("genre") String genre) {
        return ResponseEntity.ok(ApiResponse.ok("Movies retrieved", movieService.getByGenre(genre)));
    }

    @Operation(summary = "Create a movie (Admin)", description = "Adds a new movie to the catalogue. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Movie created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MovieResponse>> create(@Valid @RequestBody CreateMovieRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Movie created", movieService.create(req)));
    }

    @Operation(summary = "Update a movie (Admin)", description = "Updates movie details. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Movie updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MovieResponse>> update(
            @RequestParam("id") String id, @RequestBody UpdateMovieRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Movie updated", movieService.update(id, req)));
    }

    @Operation(summary = "Delete a movie (Admin)", description = "Removes a movie from the catalogue. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Movie deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam ("id") String id) {
        movieService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Movie deleted"));
    }

    @Operation(summary = "List all genres", description = "Returns all available movie genres.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Genres retrieved")
    @GetMapping("/genres")
    public ResponseEntity<ApiResponse<List<Genre>>> getGenres() {
        return ResponseEntity.ok(ApiResponse.ok("Genres retrieved", genreService.getAll()));
    }

    @Operation(summary = "Create a genre (Admin)",
               description = "Adds a new genre. Body: `{ \"name\": \"Action\" }`. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Genre created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/genres")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Genre>> createGenre(@RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Genre created",
                genreService.create(body.get("name"))));
    }

    @Operation(summary = "Delete a genre (Admin)", description = "Removes a genre by ID. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Genre deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @DeleteMapping("/genres")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteGenre(@RequestParam ("id") String id) {
        genreService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Genre deleted"));
    }

    @Operation(summary = "List all languages", description = "Returns all available movie languages.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Languages retrieved")
    @GetMapping("/languages")
    public ResponseEntity<ApiResponse<List<Language>>> getLanguages() {
        return ResponseEntity.ok(ApiResponse.ok("Languages retrieved", languageService.getAll()));
    }

    @Operation(summary = "Create a language (Admin)",
               description = "Adds a new language. Body: `{ \"name\": \"English\" }`. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Language created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/languages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Language>> createLanguage(@RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Language created",
                languageService.create(body.get("name"))));
    }

    @Operation(summary = "Delete a language (Admin)", description = "Removes a language by ID. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Language deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Language not found")
    })
    @DeleteMapping("/languages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLanguage(@RequestParam ("id") String id) {
        languageService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Language deleted"));
    }
}
