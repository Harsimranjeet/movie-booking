package com.movie.booking.offer.service;
import com.movie.booking.offer.dto.OfferDtos.*;
import com.movie.booking.offer.exception.BadRequestException;
import com.movie.booking.offer.exception.ResourceNotFoundException;
import com.movie.booking.offer.model.Offer;
import com.movie.booking.offer.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class OfferService {

    private final OfferRepository repo;

    public List<OfferResponse> getActive() {
        LocalDate today = LocalDate.now();
        return repo.findByActiveTrueAndValidFromLessThanEqualAndValidToGreaterThanEqual(today, today)
            .stream().map(this::toDto).toList();
    }

    public OfferResponse getById(UUID id) { return toDto(find(id)); }

    @Transactional
    public OfferResponse create(CreateOfferRequest req) {
        if (repo.findByCodeIgnoreCase(req.getCode()).isPresent())
            throw new BadRequestException("Offer code already exists: " + req.getCode());
        Offer o = Offer.builder()
            .code(req.getCode().toUpperCase()).description(req.getDescription())
            .discountType(req.getDiscountType()).discountValue(req.getDiscountValue())
            .maxDiscount(req.getMaxDiscount()).minTickets(req.getMinTickets())
            .validFrom(req.getValidFrom()).validTo(req.getValidTo())
            .maxUsesTotal(req.getMaxUsesTotal()).maxUsesPerUser(req.getMaxUsesPerUser())
            .build();
        return toDto(repo.save(o));
    }

    @Transactional
    public void deactivate(UUID id) {
        Offer o = find(id); o.setActive(false); repo.save(o);
    }

    /** Core discount calculation logic */
    @Transactional
    public DiscountResult apply(ApplyOfferRequest req) {
        Offer offer = repo.findByCodeIgnoreCase(req.getCode())
            .orElseThrow(() -> new BadRequestException("Invalid offer code: " + req.getCode()));

        LocalDate today = LocalDate.now();
        if (!offer.isActive())
            throw new BadRequestException("Offer is not active");
        if (today.isBefore(offer.getValidFrom()) || today.isAfter(offer.getValidTo()))
            throw new BadRequestException("Offer has expired or not yet started");
        if (offer.getMaxUsesTotal() > 0 && offer.getUsedCount() >= offer.getMaxUsesTotal())
            throw new BadRequestException("Offer usage limit reached");
        if (offer.getMinTickets() > 0 && req.getTicketCount() < offer.getMinTickets())
            throw new BadRequestException("Minimum " + offer.getMinTickets() + " tickets required");

        double discount = calculateDiscount(offer, req);

        // Cap discount
        if (offer.getMaxDiscount() > 0) discount = Math.min(discount, offer.getMaxDiscount());
        discount = Math.min(discount, req.getTotalAmount());

        double finalAmount = req.getTotalAmount() - discount;

        // Increment usage counter
        offer.setUsedCount(offer.getUsedCount() + 1);
        repo.save(offer);

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
