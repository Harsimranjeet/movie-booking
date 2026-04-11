package com.movie.booking.booking.dto;
import com.movie.booking.booking.model.Booking;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class BookingDtos {

    @Data public static class CreateBookingRequest {
        @NotNull
        private UUID showId;
        @NotNull
        private UUID theatreId;
        @NotNull
        private UUID movieId;
        @NotEmpty
        private List<UUID> seatIds;
        @Min(1)
        private int ticketCount;
        @Min(0)
        private double totalAmount;
        private String offerCode;
    }

    @Data
    public static class ConfirmBookingRequest {
        @NotNull
        private UUID bookingId;
        @NotNull
        private UUID paymentId;
    }

    public record BookingResponse(UUID id, UUID userId, UUID showId, UUID theatreId,
        UUID movieId, List<UUID> seatIds, int ticketCount, double totalAmount,
        double discountAmount, double finalAmount, String offerCode,
        Booking.BookingStatus status, String bookingRef,
        Instant createdAt, Instant confirmedAt, Instant cancelledAt) {}
}
