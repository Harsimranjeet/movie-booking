package com.movie.booking.offer.service;
import com.movie.booking.offer.dto.OfferDtos.*;
import com.movie.booking.offer.exception.BadRequestException;
import com.movie.booking.offer.exception.ResourceNotFoundException;
import com.movie.booking.offer.model.Offer;
import com.movie.booking.offer.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service @RequiredArgsConstructor
public class OfferService {

    private final OfferRepository repo;

    public List<OfferResponse> getActive() {
        LocalDate today = LocalDate.now();
        log.debug("Fetching active offers for date={}", today);
        List<OfferResponse> offers = repo.findByActiveTrueAndValidFromLessThanEqualAndValidToGreaterThanEqual(today, today)
            .stream().map(this::toDto).toList();
        log.debug("Found {} active offers", offers.size());
        return offers;
    }

    public OfferResponse getById(UUID id) {
        log.debug("Fetching offer by id={}", id);
        return toDto(find(id));
    }

    @Transactional
    public OfferResponse create(CreateOfferRequest req) {
        log.info("Creating offer: code='{}', type={}, value={}", req.getCode(), req.getDiscountType(), req.getDiscountValue());
        if (repo.findByCodeIgnoreCase(req.getCode()).isPresent()) {
            log.warn("Offer creation failed — code already exists: '{}'", req.getCode());
            throw new BadRequestException("Offer code already exists: " + req.getCode());
        }
        Offer o = Offer.builder()
            .code(req.getCode().toUpperCase()).description(req.getDescription())
            .discountType(req.getDiscountType()).discountValue(req.getDiscountValue())
            .maxDiscount(req.getMaxDiscount()).minTickets(req.getMinTickets())
            .validFrom(req.getValidFrom()).validTo(req.getValidTo())
            .maxUsesTotal(req.getMaxUsesTotal()).maxUsesPerUser(req.getMaxUsesPerUser())
            .build();
        OfferResponse saved = toDto(repo.save(o));
        log.info("Offer created: id={}, code='{}'", saved.getId(), saved.getCode());
        return saved;
    }

    @Transactional
    public void deactivate(UUID id) {
        log.info("Deactivating offer: id={}", id);
        Offer o = find(id); o.setActive(false); repo.save(o);
        log.info("Offer deactivated: id={}, code='{}'", id, o.getCode());
    }

    /** Core discount calculation logic */
    @Transactional
    public DiscountResult apply(ApplyOfferRequest req) {
        log.info("Applying offer: code='{}', totalAmount={}, ticketCount={}", req.getCode(), req.getTotalAmount(), req.getTicketCount());
        Offer offer = repo.findByCodeIgnoreCase(req.getCode())
            .orElseThrow(() -> {
                log.warn("Offer application failed — invalid code: '{}'", req.getCode());
                return new BadRequestException("Invalid offer code: " + req.getCode());
            });

        LocalDate today = LocalDate.now();
        if (!offer.isActive()) {
            log.warn("Offer application failed — offer inactive: code='{}'", req.getCode());
            throw new BadRequestException("Offer is not active");
        }
        if (today.isBefore(offer.getValidFrom()) || today.isAfter(offer.getValidTo())) {
            log.warn("Offer application failed — offer expired or not started: code='{}', validFrom={}, validTo={}", req.getCode(), offer.getValidFrom(), offer.getValidTo());
            throw new BadRequestException("Offer has expired or not yet started");
        }
        if (offer.getMaxUsesTotal() > 0 && offer.getUsedCount() >= offer.getMaxUsesTotal()) {
            log.warn("Offer application failed — usage limit reached: code='{}', usedCount={}, max={}", req.getCode(), offer.getUsedCount(), offer.getMaxUsesTotal());
            throw new BadRequestException("Offer usage limit reached");
        }
        if (offer.getMinTickets() > 0 && req.getTicketCount() < offer.getMinTickets()) {
            log.warn("Offer application failed — not enough tickets: code='{}', required={}, provided={}", req.getCode(), offer.getMinTickets(), req.getTicketCount());
            throw new BadRequestException("Minimum " + offer.getMinTickets() + " tickets required");
        }

        double discount = calculateDiscount(offer, req);

        // Cap discount
        if (offer.getMaxDiscount() > 0) discount = Math.min(discount, offer.getMaxDiscount());
        discount = Math.min(discount, req.getTotalAmount());

        double finalAmount = req.getTotalAmount() - discount;

        // Increment usage counter
        offer.setUsedCount(offer.getUsedCount() + 1);
        repo.save(offer);

        log.info("Offer applied: code='{}', originalAmount={}, discount={}, finalAmount={}", offer.getCode(), req.getTotalAmount(), discount, finalAmount);
        return new DiscountResult(offer.getCode(), req.getTotalAmount(),
            Math.round(discount * 100.0) / 100.0,
            Math.round(finalAmount * 100.0) / 100.0,
            "Offer applied: " + offer.getDescription());
    }

    private double calculateDiscount(Offer offer, ApplyOfferRequest req) {
        return switch (offer.getDiscountType()) {
            case PERCENTAGE   -> req.getTotalAmount() * offer.getDiscountValue() / 100.0;
            case FLAT         -> offer.getDiscountValue();
            case NTH_TICKET   -> {
                // 50% off every Nth ticket
                int n = offer.getMinTickets() > 0 ? offer.getMinTickets() : 3;
                int discountedTickets = req.getTicketCount() / n;
                double perTicket = req.getTotalAmount() / req.getTicketCount();
                yield discountedTickets * perTicket * (offer.getDiscountValue() / 100.0);
            }
            case MATINEE -> {
                // 20% off for afternoon shows (12:00–17:00)
                LocalTime t = req.getShowTime();
                if (t != null && !t.isBefore(LocalTime.of(12,0)) && t.isBefore(LocalTime.of(17,0)))
                    yield req.getTotalAmount() * offer.getDiscountValue() / 100.0;
                yield 0;
            }
        };
    }

    private Offer find(UUID id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Offer not found: " + id));
    }
    private OfferResponse toDto(Offer o) {
        return new OfferResponse(o.getId(),o.getCode(),o.getDescription(),o.getDiscountType(),
            o.getDiscountValue(),o.getMaxDiscount(),o.getMinTickets(),o.getValidFrom(),
            o.getValidTo(),o.getMaxUsesTotal(),o.getUsedCount(),o.isActive());
    }
}
