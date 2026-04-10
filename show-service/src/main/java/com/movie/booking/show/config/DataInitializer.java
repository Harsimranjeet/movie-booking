package com.movie.booking.show.config;

import com.movie.booking.show.model.Show;
import com.movie.booking.show.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final ShowRepository showRepo;

    // ── Movie UUIDs (match movie-service seed) ─────────────────────────────────
    private static final UUID M1 = UUID.fromString("10000000-0000-0000-0000-000000000001"); // Inception
    private static final UUID M2 = UUID.fromString("10000000-0000-0000-0000-000000000002"); // Interstellar
    private static final UUID M3 = UUID.fromString("10000000-0000-0000-0000-000000000003"); // Dark Knight
    private static final UUID M4 = UUID.fromString("10000000-0000-0000-0000-000000000004"); // Avengers
    private static final UUID M5 = UUID.fromString("10000000-0000-0000-0000-000000000005"); // RRR
    private static final UUID M6 = UUID.fromString("10000000-0000-0000-0000-000000000006"); // Oppenheimer
    private static final UUID M7 = UUID.fromString("10000000-0000-0000-0000-000000000007"); // Pathaan
    private static final UUID M8 = UUID.fromString("10000000-0000-0000-0000-000000000008"); // KGF 2
    private static final UUID M9 = UUID.fromString("10000000-0000-0000-0000-000000000009"); // Brahmastra
    private static final UUID M10= UUID.fromString("10000000-0000-0000-0000-00000000000a"); // Animal

    // ── Theatre UUIDs (match theatre-service seed) ─────────────────────────────
    private static final UUID T1 = UUID.fromString("20000000-0000-0000-0000-000000000001"); // PVR Mumbai
    private static final UUID T2 = UUID.fromString("20000000-0000-0000-0000-000000000002"); // INOX Delhi
    private static final UUID T3 = UUID.fromString("20000000-0000-0000-0000-000000000003"); // Cinepolis Bangalore
    private static final UUID T4 = UUID.fromString("20000000-0000-0000-0000-000000000004"); // Rohini Chennai
    private static final UUID T5 = UUID.fromString("20000000-0000-0000-0000-000000000005"); // INOX Kolkata

    // ── Screen UUIDs (match theatre-service seed) ──────────────────────────────
    private static final UUID SC1 = UUID.fromString("30000000-0000-0000-0000-000000000001"); // T1 Reg
    private static final UUID SC2 = UUID.fromString("30000000-0000-0000-0000-000000000002"); // T1 IMAX
    private static final UUID SC3 = UUID.fromString("30000000-0000-0000-0000-000000000003"); // T1 Reg
    private static final UUID SC4 = UUID.fromString("30000000-0000-0000-0000-000000000004"); // T2 Reg
    private static final UUID SC5 = UUID.fromString("30000000-0000-0000-0000-000000000005"); // T2 DOLBY
    private static final UUID SC6 = UUID.fromString("30000000-0000-0000-0000-000000000006"); // T3 Reg
    private static final UUID SC7 = UUID.fromString("30000000-0000-0000-0000-000000000007"); // T3 FOUR_DX
    private static final UUID SC8 = UUID.fromString("30000000-0000-0000-0000-000000000008"); // T4 Reg
    private static final UUID SC9 = UUID.fromString("30000000-0000-0000-0000-000000000009"); // T5 Reg

    // ── Fixed Show UUIDs (referenced by seat-service and booking-service seed) ─
    static final UUID SH01 = UUID.fromString("40000000-0000-0000-0000-000000000001");
    static final UUID SH02 = UUID.fromString("40000000-0000-0000-0000-000000000002");
    static final UUID SH03 = UUID.fromString("40000000-0000-0000-0000-000000000003");
    static final UUID SH04 = UUID.fromString("40000000-0000-0000-0000-000000000004");
    static final UUID SH05 = UUID.fromString("40000000-0000-0000-0000-000000000005");
    static final UUID SH06 = UUID.fromString("40000000-0000-0000-0000-000000000006");
    static final UUID SH07 = UUID.fromString("40000000-0000-0000-0000-000000000007");
    static final UUID SH08 = UUID.fromString("40000000-0000-0000-0000-000000000008");
    static final UUID SH09 = UUID.fromString("40000000-0000-0000-0000-000000000009");
    static final UUID SH10 = UUID.fromString("40000000-0000-0000-0000-00000000000a");
    static final UUID SH11 = UUID.fromString("40000000-0000-0000-0000-00000000000b");
    static final UUID SH12 = UUID.fromString("40000000-0000-0000-0000-00000000000c");
    static final UUID SH13 = UUID.fromString("40000000-0000-0000-0000-00000000000d");
    static final UUID SH14 = UUID.fromString("40000000-0000-0000-0000-00000000000e");
    static final UUID SH15 = UUID.fromString("40000000-0000-0000-0000-00000000000f");
    static final UUID SH16 = UUID.fromString("40000000-0000-0000-0000-000000000010");
    static final UUID SH17 = UUID.fromString("40000000-0000-0000-0000-000000000011");
    static final UUID SH18 = UUID.fromString("40000000-0000-0000-0000-000000000012");

    @Override
    public void run(ApplicationArguments args) {
        if (showRepo.count() > 0) return;

        LocalDate today    = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dayAfter = today.plusDays(2);

        // ── TODAY ────────────────────────────────────────────────────────────────
        show(SH01, M1, T1, SC1, today, "09:00","11:28", "English", "2D",    200.0, Show.ShowStatus.OPEN);
        show(SH02, M1, T1, SC2, today, "14:00","16:28", "English", "IMAX",  400.0, Show.ShowStatus.OPEN);
        show(SH03, M2, T2, SC4, today, "10:00","12:49", "English", "2D",    220.0, Show.ShowStatus.OPEN);
        show(SH04, M3, T3, SC6, today, "11:00","13:32", "English", "2D",    210.0, Show.ShowStatus.OPEN);
        show(SH05, M3, T3, SC7, today, "19:00","21:32", "English", "FOUR_DX",550.0, Show.ShowStatus.OPEN);
        show(SH06, M4, T1, SC3, today, "12:00","15:01", "English", "2D",    250.0, Show.ShowStatus.HOUSEFULL);
        show(SH07, M5, T4, SC8, today, "09:30","13:07", "Telugu",  "2D",    180.0, Show.ShowStatus.OPEN);
        show(SH08, M6, T1, SC2, today, "20:00","23:00", "English", "IMAX",  450.0, Show.ShowStatus.OPEN);
        show(SH09, M7, T5, SC9, today, "15:00","17:26", "Hindi",   "2D",    170.0, Show.ShowStatus.OPEN);
        show(SH10, M8, T3, SC6, today, "16:00","18:48", "Kannada", "2D",    190.0, Show.ShowStatus.OPEN);

        // ── TOMORROW ─────────────────────────────────────────────────────────────
        show(SH11, M1, T1, SC1, tomorrow, "09:00","11:28", "English", "2D",    200.0, Show.ShowStatus.SCHEDULED);
        show(SH12, M2, T2, SC5, tomorrow, "14:00","16:49", "English", "DOLBY", 350.0, Show.ShowStatus.SCHEDULED);
        show(SH13, M5, T4, SC8, tomorrow, "10:00","13:37", "Telugu",  "2D",    180.0, Show.ShowStatus.SCHEDULED);
        show(SH14, M9, T2, SC4, tomorrow, "18:00","20:47", "Hindi",   "2D",    200.0, Show.ShowStatus.SCHEDULED);
        show(SH15, M10,T5, SC9, tomorrow, "19:30","23:01", "Hindi",   "2D",    220.0, Show.ShowStatus.SCHEDULED);

        // ── DAY AFTER TOMORROW ───────────────────────────────────────────────────
        show(SH16, M3, T2, SC4, dayAfter, "11:00","13:32", "English", "2D",    210.0, Show.ShowStatus.SCHEDULED);
        show(SH17, M6, T1, SC2, dayAfter, "18:00","21:00", "English", "IMAX",  450.0, Show.ShowStatus.SCHEDULED);
        show(SH18, M4, T3, SC7, dayAfter, "14:00","17:01", "English", "FOUR_DX",600.0,Show.ShowStatus.SCHEDULED);

        log.info("Show-service seed data loaded — {} shows created.", showRepo.count());
    }

    private void show(UUID id, UUID movieId, UUID theatreId, UUID screenId,
                      LocalDate date, String start, String end,
                      String lang, String format, double price, Show.ShowStatus status) {
        Show s = Show.builder()
            .movieId(movieId).theatreId(theatreId).screenId(screenId)
            .showDate(date)
            .startTime(LocalTime.parse(start))
            .endTime(LocalTime.parse(end))
            .language(lang).format(format).basePrice(price).status(status).build();
        s.setId(id);
        showRepo.save(s);
    }
}
