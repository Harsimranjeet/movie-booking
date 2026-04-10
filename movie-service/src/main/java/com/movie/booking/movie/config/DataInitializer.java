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
        languageRepo.save(Language.builder().code("en").name("English").build());
        languageRepo.save(Language.builder().code("hi").name("Hindi").build());
        languageRepo.save(Language.builder().code("ta").name("Tamil").build());
        languageRepo.save(Language.builder().code("te").name("Telugu").build());
        languageRepo.save(Language.builder().code("kn").name("Kannada").build());
    }

    private void seedGenres() {
        genreRepo.save(Genre.builder().name("Action").description("High-octane action films").build());
        genreRepo.save(Genre.builder().name("Sci-Fi").description("Science fiction and futuristic stories").build());
        genreRepo.save(Genre.builder().name("Drama").description("Character-driven emotional narratives").build());
        genreRepo.save(Genre.builder().name("Thriller").description("Suspense and tension-filled stories").build());
        genreRepo.save(Genre.builder().name("Comedy").description("Light-hearted and humorous films").build());
        genreRepo.save(Genre.builder().name("Biography").description("Based on real-life stories").build());
    }

    private void seedMovies() {
        // Inception
        Movie m1 = Movie.builder()
            .title("Inception").language("English").genre("Sci-Fi")
            .description("A thief who enters the dreams of others to steal secrets from their subconscious.")
            .durationMins(148).certification("UA").rating(8.8)
            .posterUrl("https://example.com/posters/inception.jpg")
            .trailerUrl("https://youtube.com/watch?v=inception").build();
        m1.setId(M1); movieRepo.save(m1);

        // Interstellar
        Movie m2 = Movie.builder()
            .title("Interstellar").language("English").genre("Sci-Fi")
            .description("A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.")
            .durationMins(169).certification("UA").rating(8.6)
            .posterUrl("https://example.com/posters/interstellar.jpg")
            .trailerUrl("https://youtube.com/watch?v=interstellar").build();
        m2.setId(M2); movieRepo.save(m2);

        // The Dark Knight
        Movie m3 = Movie.builder()
            .title("The Dark Knight").language("English").genre("Action")
            .description("Batman faces the Joker, a criminal mastermind who wants to plunge Gotham City into anarchy.")
            .durationMins(152).certification("UA").rating(9.0)
            .posterUrl("https://example.com/posters/dark-knight.jpg")
            .trailerUrl("https://youtube.com/watch?v=darkknight").build();
        m3.setId(M3); movieRepo.save(m3);

        // Avengers: Endgame
        Movie m4 = Movie.builder()
            .title("Avengers: Endgame").language("English").genre("Action")
            .description("After the devastating events of Infinity War, the Avengers assemble once more to reverse Thanos's actions.")
            .durationMins(181).certification("UA").rating(8.4)
            .posterUrl("https://example.com/posters/endgame.jpg")
            .trailerUrl("https://youtube.com/watch?v=endgame").build();
        m4.setId(M4); movieRepo.save(m4);

        // RRR
        Movie m5 = Movie.builder()
            .title("RRR").language("Telugu").genre("Action")
            .description("A fictitious story about two legendary revolutionaries and their journey away from home.")
            .durationMins(187).certification("UA").rating(8.0)
            .posterUrl("https://example.com/posters/rrr.jpg")
            .trailerUrl("https://youtube.com/watch?v=rrr").build();
        m5.setId(M5); movieRepo.save(m5);

        // Oppenheimer
        Movie m6 = Movie.builder()
            .title("Oppenheimer").language("English").genre("Biography")
            .description("The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb.")
            .durationMins(180).certification("A").rating(8.5)
            .posterUrl("https://example.com/posters/oppenheimer.jpg")
            .trailerUrl("https://youtube.com/watch?v=oppenheimer").build();
        m6.setId(M6); movieRepo.save(m6);

        // Pathaan
        Movie m7 = Movie.builder()
            .title("Pathaan").language("Hindi").genre("Action")
            .description("An Indian spy takes on the leader of a rogue mercenary organisation.")
            .durationMins(146).certification("UA").rating(6.0)
            .posterUrl("https://example.com/posters/pathaan.jpg")
            .trailerUrl("https://youtube.com/watch?v=pathaan").build();
        m7.setId(M7); movieRepo.save(m7);

        // KGF Chapter 2
        Movie m8 = Movie.builder()
            .title("KGF: Chapter 2").language("Kannada").genre("Action")
            .description("Rocky takes control of the Kolar Gold Fields and his bloodlust brings him into conflict with Ramika Sen.")
            .durationMins(168).certification("A").rating(8.2)
            .posterUrl("https://example.com/posters/kgf2.jpg")
            .trailerUrl("https://youtube.com/watch?v=kgf2").build();
        m8.setId(M8); movieRepo.save(m8);

        // Brahmastra
        Movie m9 = Movie.builder()
            .title("Brahmastra: Part One – Shiva").language("Hindi").genre("Action")
            .description("Shiva discovers he has a strange connection with the element of Fire and the ancient weapon Brahmastra.")
            .durationMins(167).certification("UA").rating(5.6)
            .posterUrl("https://example.com/posters/brahmastra.jpg")
            .trailerUrl("https://youtube.com/watch?v=brahmastra").build();
        m9.setId(M9); movieRepo.save(m9);

        // Animal
        Movie m10 = Movie.builder()
            .title("Animal").language("Hindi").genre("Action")
            .description("A son's all-consuming obsession with his estranged father leads him down a dark path of violence.")
            .durationMins(201).certification("A").rating(7.2)
            .posterUrl("https://example.com/posters/animal.jpg")
            .trailerUrl("https://youtube.com/watch?v=animal").build();
        m10.setId(M10); movieRepo.save(m10);
    }
}
