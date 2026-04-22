package com.movie.booking.movie.config;

import com.movie.booking.movie.model.Genre;
import com.movie.booking.movie.model.Language;
import com.movie.booking.movie.model.Movie;
import com.movie.booking.movie.repository.GenreRepository;
import com.movie.booking.movie.repository.LanguageRepository;
import com.movie.booking.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final MovieRepository    movieRepo;
    private final GenreRepository    genreRepo;
    private final LanguageRepository languageRepo;

    // ── Fixed UUIDs (shared across services) ──────────────────────────────────
    static final UUID M1  = UUID.fromString("10000000-0000-0000-0000-000000000001");
    static final UUID M2  = UUID.fromString("10000000-0000-0000-0000-000000000002");
    static final UUID M3  = UUID.fromString("10000000-0000-0000-0000-000000000003");
    static final UUID M4  = UUID.fromString("10000000-0000-0000-0000-000000000004");
    static final UUID M5  = UUID.fromString("10000000-0000-0000-0000-000000000005");
    static final UUID M6  = UUID.fromString("10000000-0000-0000-0000-000000000006");
    static final UUID M7  = UUID.fromString("10000000-0000-0000-0000-000000000007");
    static final UUID M8  = UUID.fromString("10000000-0000-0000-0000-000000000008");
    static final UUID M9  = UUID.fromString("10000000-0000-0000-0000-000000000009");
    static final UUID M10 = UUID.fromString("10000000-0000-0000-0000-00000000000a");

    @Override
    public void run(ApplicationArguments args) {
        if (movieRepo.count() > 0) return;
        seedLanguages();
        seedGenres();
        seedMovies();
        log.info("Movie-service seed data loaded.");
    }

    private void seedLanguages() {
        languageRepo.save(Language.builder().name("English").build());
        languageRepo.save(Language.builder().name("Hindi").build());
        languageRepo.save(Language.builder().name("Tamil").build());
        languageRepo.save(Language.builder().name("Telugu").build());
        languageRepo.save(Language.builder().name("Kannada").build());
    }

    private void seedGenres() {
        genreRepo.save(Genre.builder().name("Action").build());
        genreRepo.save(Genre.builder().name("Sci-Fi").build());
        genreRepo.save(Genre.builder().name("Drama").build());
        genreRepo.save(Genre.builder().name("Thriller").build());
        genreRepo.save(Genre.builder().name("Comedy").build());
        genreRepo.save(Genre.builder().name("Biography").build());
    }

    private void seedMovies() {
        Movie m1 = Movie.builder().title("Inception").language("English").genre("Sci-Fi").durationMins(148).rating(8.8).build();
        m1.setId(M1); movieRepo.save(m1);

        Movie m2 = Movie.builder().title("Interstellar").language("English").genre("Sci-Fi").durationMins(169).rating(8.6).build();
        m2.setId(M2); movieRepo.save(m2);

        Movie m3 = Movie.builder().title("The Dark Knight").language("English").genre("Action").durationMins(152).rating(9.0).build();
        m3.setId(M3); movieRepo.save(m3);

        Movie m4 = Movie.builder().title("Avengers: Endgame").language("English").genre("Action").durationMins(181).rating(8.4).build();
        m4.setId(M4); movieRepo.save(m4);

        Movie m5 = Movie.builder().title("RRR").language("Telugu").genre("Action").durationMins(187).rating(8.0).build();
        m5.setId(M5); movieRepo.save(m5);

        Movie m6 = Movie.builder().title("Oppenheimer").language("English").genre("Biography").durationMins(180).rating(8.5).build();
        m6.setId(M6); movieRepo.save(m6);

        Movie m7 = Movie.builder().title("Pathaan").language("Hindi").genre("Action").durationMins(146).rating(6.0).build();
        m7.setId(M7); movieRepo.save(m7);

        Movie m8 = Movie.builder().title("KGF: Chapter 2").language("Kannada").genre("Action").durationMins(168).rating(8.2).build();
        m8.setId(M8); movieRepo.save(m8);

        Movie m9 = Movie.builder().title("Brahmastra: Part One – Shiva").language("Hindi").genre("Action").durationMins(167).rating(5.6).build();
        m9.setId(M9); movieRepo.save(m9);

        Movie m10 = Movie.builder().title("Animal").language("Hindi").genre("Action").durationMins(201).rating(7.2).build();
        m10.setId(M10); movieRepo.save(m10);
    }
}
