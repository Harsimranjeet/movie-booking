package com.movie.booking.offer.config;

import com.movie.booking.offer.model.Offer;
import com.movie.booking.offer.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final OfferRepository offerRepo;

    @Override
    public void run(ApplicationArguments args) {
        if (offerRepo.count() > 0) return;

        LocalDate today  = LocalDate.now();
        LocalDate end    = today.plusMonths(3);
        LocalDate endYr  = today.plusYears(1);

        // 20% off (max ₹100) for 2+ tickets
        offer("SUMMER20", "20% off on 2 or more tickets this summer",
            Offer.DiscountType.PERCENTAGE, 20, 100, 2, today, end, 500, 1);

        // ₹150 flat off on 3+ tickets
        offer("FLAT150", "Flat ₹150 off on booking 3 or more tickets",
            Offer.DiscountType.FLAT, 150, 150, 3, today, endYr, 1000, 2);

        // 30% off for first-time users (max ₹120)
        offer("FIRSTSHOW", "30% off on your first booking",
            Offer.DiscountType.PERCENTAGE, 30, 120, 1, today, endYr, 200, 1);

        // Buy 3 get 1 free (4th ticket free)
        offer("BUY3GET1", "Buy 3 tickets, get the 4th one free",
            Offer.DiscountType.NTH_TICKET, 1, 300, 4, today, end, 300, 2);

        // Matinee discount – flat ₹50 off (morning shows)
        offer("MATINEE50", "₹50 off on morning shows before 12 PM",
            Offer.DiscountType.MATINEE, 50, 50, 1, today, endYr, 2000, 3);

        // Weekend 15% off (max ₹80)
        offer("WEEKEND15", "15% off on weekend bookings",
            Offer.DiscountType.PERCENTAGE, 15, 80, 1, today, end, 800, 2);

        // Student discount – 25% off (max ₹75), 2+ tickets
        offer("STUDENT25", "25% off for students on 2+ tickets",
            Offer.DiscountType.PERCENTAGE, 25, 75, 2, today, endYr, 500, 1);

        log.info("Offer-service seed data loaded — 7 offers created.");
    }

    private void offer(String code, String desc, Offer.DiscountType type,
                       double value, double maxDiscount, int minTickets,
                       LocalDate from, LocalDate to, int maxTotal, int maxPerUser) {
        offerRepo.save(Offer.builder()
            .code(code).description(desc).discountType(type)
            .discountValue(value).maxDiscount(maxDiscount).minTickets(minTickets)
            .validFrom(from).validTo(to)
            .maxUsesTotal(maxTotal).maxUsesPerUser(maxPerUser).build());
    }
}
