package com.movie.booking.seat.config;

import com.movie.booking.seat.model.Seat;
import com.movie.booking.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final SeatRepository seatRepo;

    // ── Show UUIDs (match show-service seed) ───────────────────────────────────
    private static final UUID SH01 = UUID.fromString("40000000-0000-0000-0000-000000000001"); // Inception T1 SC1
    private static final UUID SH02 = UUID.fromString("40000000-0000-0000-0000-000000000002"); // Inception T1 IMAX
    private static final UUID SH03 = UUID.fromString("40000000-0000-0000-0000-000000000003"); // Interstellar T2 SC4
    private static final UUID SH04 = UUID.fromString("40000000-0000-0000-0000-000000000004"); // Dark Knight T3 SC6
    private static final UUID SH05 = UUID.fromString("40000000-0000-0000-0000-000000000005"); // Dark Knight T3 FOUR_DX
    private static final UUID SH06 = UUID.fromString("40000000-0000-0000-0000-000000000006"); // Avengers T1 SC3
    private static final UUID SH07 = UUID.fromString("40000000-0000-0000-0000-000000000007"); // RRR T4 SC8
    private static final UUID SH08 = UUID.fromString("40000000-0000-0000-0000-000000000008"); // Oppenheimer T1 IMAX
    private static final UUID SH09 = UUID.fromString("40000000-0000-0000-0000-000000000009"); // Pathaan T5 SC9
    private static final UUID SH10 = UUID.fromString("40000000-0000-0000-0000-00000000000a"); // KGF T3 SC6

    // ── Screen UUIDs (match theatre-service seed) ──────────────────────────────
    private static final UUID SC1 = UUID.fromString("30000000-0000-0000-0000-000000000001");
    private static final UUID SC2 = UUID.fromString("30000000-0000-0000-0000-000000000002");
    private static final UUID SC3 = UUID.fromString("30000000-0000-0000-0000-000000000003");
    private static final UUID SC4 = UUID.fromString("30000000-0000-0000-0000-000000000004");
    private static final UUID SC5 = UUID.fromString("30000000-0000-0000-0000-000000000005");
    private static final UUID SC6 = UUID.fromString("30000000-0000-0000-0000-000000000006");
    private static final UUID SC7 = UUID.fromString("30000000-0000-0000-0000-000000000007");
    private static final UUID SC8 = UUID.fromString("30000000-0000-0000-0000-000000000008");
    private static final UUID SC9 = UUID.fromString("30000000-0000-0000-0000-000000000009");

    @Override
    public void run(ApplicationArguments args) {
        if (seatRepo.count() > 0) return;

        //  Layout per screen type:
        //  REGULAR screen (SC1/SC3/SC4/SC6/SC8/SC9):
        //    Rows A-B → VIP (₹price×1.5), C-D → PREMIUM (₹price×1.2), E-H → REGULAR (₹price)
        //  IMAX screen (SC2): rows A-B VIP, C-E PREMIUM, F-J REGULAR, 12 seats/row
        //  FOUR_DX screen (SC7): all RECLINER, 10 cols, rows A-H
        //  DOLBY screen (SC5): rows A-B VIP, C-D PREMIUM, E-G REGULAR, 10 seats/row

        seedRegularScreen(SH01, SC1, 200.0, 12); // 8 rows × 12 = 96 seats
        seedIMAXScreen   (SH02, SC2, 400.0);      // 10 rows × 12 = 120 seats
        seedRegularScreen(SH03, SC4, 220.0, 12);
        seedRegularScreen(SH04, SC6, 210.0, 12);
        seedFourDXScreen (SH05, SC7, 550.0);      // 8 rows × 10 = 80 seats (all RECLINER)
        seedRegularScreen(SH06, SC3, 250.0, 10);  // 8 rows × 10 = 80 seats
        seedRegularScreen(SH07, SC8, 180.0, 10);
        seedIMAXScreen   (SH08, SC2, 450.0);      // same screen, different show
        seedRegularScreen(SH09, SC9, 170.0, 10);
        seedRegularScreen(SH10, SC6, 190.0, 12);

        log.info("Seat-service seed data loaded — {} seats created.", seatRepo.count());
    }

    /** 8 rows (A–H), configurable columns. A-B=VIP, C-D=PREMIUM, E-H=REGULAR */
    private void seedRegularScreen(UUID showId, UUID screenId, double basePrice, int cols) {
        List<Seat> seats = new ArrayList<>();
        String[] rows = {"A","B","C","D","E","F","G","H"};
        for (String row : rows) {
            Seat.SeatCategory cat = switch (row) {
                case "A", "B" -> Seat.SeatCategory.VIP;
                case "C", "D" -> Seat.SeatCategory.PREMIUM;
                default        -> Seat.SeatCategory.REGULAR;
            };
            double price = switch (cat) {
                case VIP     -> basePrice * 1.5;
                case PREMIUM -> basePrice * 1.2;
                default      -> basePrice;
            };
            for (int col = 1; col <= cols; col++) {
                seats.add(Seat.builder()
                    .showId(showId).screenId(screenId)
                    .rowLabel(row).seatNumber(row + col)
                    .category(cat).price(price).build());
            }
        }
        seatRepo.saveAll(seats);
    }

    /** 10 rows (A–J), 12 cols. A-B=VIP, C-E=PREMIUM, F-J=REGULAR */
    private void seedIMAXScreen(UUID showId, UUID screenId, double basePrice) {
        List<Seat> seats = new ArrayList<>();
        String[] rows = {"A","B","C","D","E","F","G","H","I","J"};
        for (String row : rows) {
            Seat.SeatCategory cat = switch (row) {
                case "A", "B"               -> Seat.SeatCategory.VIP;
                case "C", "D", "E"          -> Seat.SeatCategory.PREMIUM;
                default                     -> Seat.SeatCategory.REGULAR;
            };
            double price = switch (cat) {
                case VIP     -> basePrice * 1.4;
                case PREMIUM -> basePrice * 1.2;
                default      -> basePrice;
            };
            for (int col = 1; col <= 12; col++) {
                seats.add(Seat.builder()
                    .showId(showId).screenId(screenId)
                    .rowLabel(row).seatNumber(row + col)
                    .category(cat).price(price).build());
            }
        }
        seatRepo.saveAll(seats);
    }

    /** 8 rows (A–H), 10 cols, all RECLINER */
    private void seedFourDXScreen(UUID showId, UUID screenId, double basePrice) {
        List<Seat> seats = new ArrayList<>();
        for (char r = 'A'; r <= 'H'; r++) {
            for (int col = 1; col <= 10; col++) {
                seats.add(Seat.builder()
                    .showId(showId).screenId(screenId)
                    .rowLabel(String.valueOf(r)).seatNumber(r + "" + col)
                    .category(Seat.SeatCategory.RECLINER).price(basePrice).build());
            }
        }
        seatRepo.saveAll(seats);
    }
}
