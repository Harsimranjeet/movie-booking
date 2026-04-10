package com.movie.booking.theatre.config;

import com.movie.booking.theatre.model.Screen;
import com.movie.booking.theatre.model.Theatre;
import com.movie.booking.theatre.repository.ScreenRepository;
import com.movie.booking.theatre.repository.TheatreRepository;
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

    private final TheatreRepository theatreRepo;
    private final ScreenRepository  screenRepo;

    // ── Fixed Theatre UUIDs ────────────────────────────────────────────────────
    static final UUID T1 = UUID.fromString("20000000-0000-0000-0000-000000000001");
    static final UUID T2 = UUID.fromString("20000000-0000-0000-0000-000000000002");
    static final UUID T3 = UUID.fromString("20000000-0000-0000-0000-000000000003");
    static final UUID T4 = UUID.fromString("20000000-0000-0000-0000-000000000004");
    static final UUID T5 = UUID.fromString("20000000-0000-0000-0000-000000000005");

    // ── Fixed Screen UUIDs ─────────────────────────────────────────────────────
    static final UUID SC1 = UUID.fromString("30000000-0000-0000-0000-000000000001"); // T1 Screen 1
    static final UUID SC2 = UUID.fromString("30000000-0000-0000-0000-000000000002"); // T1 Screen 2 IMAX
    static final UUID SC3 = UUID.fromString("30000000-0000-0000-0000-000000000003"); // T1 Screen 3
    static final UUID SC4 = UUID.fromString("30000000-0000-0000-0000-000000000004"); // T2 Screen 1
    static final UUID SC5 = UUID.fromString("30000000-0000-0000-0000-000000000005"); // T2 Screen 2 DOLBY
    static final UUID SC6 = UUID.fromString("30000000-0000-0000-0000-000000000006"); // T3 Screen 1
    static final UUID SC7 = UUID.fromString("30000000-0000-0000-0000-000000000007"); // T3 Screen 2 FOUR_DX
    static final UUID SC8 = UUID.fromString("30000000-0000-0000-0000-000000000008"); // T4 Screen 1
    static final UUID SC9 = UUID.fromString("30000000-0000-0000-0000-000000000009"); // T5 Screen 1

    @Override
    public void run(ApplicationArguments args) {
        if (theatreRepo.count() > 0) return;
        seedTheatres();
        seedScreens();
        log.info("Theatre-service seed data loaded.");
    }

    private void seedTheatres() {
        Theatre t1 = Theatre.builder()
            .name("PVR Phoenix Mills").address("Lower Parel, Phoenix Mills Compound")
            .city("Mumbai").state("Maharashtra").pincode("400013")
            .phone("+912244556677").email("pvr.phoenix@pvrinemas.com").build();
        t1.setId(T1); theatreRepo.save(t1);

        Theatre t2 = Theatre.builder()
            .name("INOX Saket").address("Select City Walk Mall, Saket District Centre")
            .city("Delhi").state("Delhi").pincode("110017")
            .phone("+911145678901").email("inox.saket@inoxmovies.com").build();
        t2.setId(T2); theatreRepo.save(t2);

        Theatre t3 = Theatre.builder()
            .name("Cinepolis Koramangala").address("1 MG Road, Koramangala")
            .city("Bangalore").state("Karnataka").pincode("560034")
            .phone("+918023456789").email("cinepolis.koramangala@cinepolis.com").build();
        t3.setId(T3); theatreRepo.save(t3);

        Theatre t4 = Theatre.builder()
            .name("Rohini Silver Screens").address("3 Vadapalani, Chennai")
            .city("Chennai").state("Tamil Nadu").pincode("600026")
            .phone("+914423456789").email("rohini.silver@gmail.com").build();
        t4.setId(T4); theatreRepo.save(t4);

        Theatre t5 = Theatre.builder()
            .name("INOX Forum Mall").address("10/3 Elgin Road, Forum Mall")
            .city("Kolkata").state("West Bengal").pincode("700020")
            .phone("+913323456789").email("inox.forum@inoxmovies.com").build();
        t5.setId(T5); theatreRepo.save(t5);
    }

    private void seedScreens() {
        // PVR Phoenix (T1) – 3 screens
        screen(SC1, T1, "Screen 1",  150, Screen.ScreenType.REGULAR);
        screen(SC2, T1, "Screen 2",  180, Screen.ScreenType.IMAX);
        screen(SC3, T1, "Screen 3",  120, Screen.ScreenType.REGULAR);

        // INOX Saket (T2) – 2 screens
        screen(SC4, T2, "Screen 1",  140, Screen.ScreenType.REGULAR);
        screen(SC5, T2, "Screen 2",  100, Screen.ScreenType.DOLBY);

        // Cinepolis Koramangala (T3) – 2 screens
        screen(SC6, T3, "Screen 1",  130, Screen.ScreenType.REGULAR);
        screen(SC7, T3, "Screen 2",   80, Screen.ScreenType.FOUR_DX);

        // Rohini Silver Screens (T4)
        screen(SC8, T4, "Screen 1",  120, Screen.ScreenType.REGULAR);

        // INOX Forum Mall (T5)
        screen(SC9, T5, "Screen 1",  110, Screen.ScreenType.REGULAR);
    }

    private void screen(UUID id, UUID theatreId, String name, int seats, Screen.ScreenType type) {
        Screen s = Screen.builder().theatreId(theatreId).name(name).totalSeats(seats).type(type).build();
        s.setId(id);
        screenRepo.save(s);
    }
}
