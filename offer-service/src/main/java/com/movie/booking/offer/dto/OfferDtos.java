package com.movie.booking.offer.dto;
import com.movie.booking.offer.model.Offer;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

public class OfferDtos {

    @Data public static class CreateOfferRequest {
        @NotBlank private String code;
        @NotBlank private String description;
        @NotNull  private Offer.DiscountType discountType;
        @Min(0)   private double discountValue;
        private double maxDiscount;
        private int    minTickets;
        @NotNull private LocalDate validFrom;
        @NotNull private LocalDate validTo;
        private int maxUsesTotal;
        private int maxUsesPerUser;
    }

    @Data public static class ApplyOfferRequest {
        @NotBlank private String code;
        @Min(1)   private int ticketCount;
        @Min(0)   private double totalAmount;
        @NotNull  private java.time.LocalTime showTime;
    }

    public record OfferResponse(UUID id, String code, String description,
        Offer.DiscountType discountType, double discountValue, double maxDiscount,
        int minTickets, LocalDate validFrom, LocalDate validTo,
        int maxUsesTotal, int usedCount, boolean active) {}

    public record DiscountResult(String offerCode, double originalAmount,
        double discountAmount, double finalAmount, String message) {}
}
