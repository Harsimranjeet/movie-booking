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
        @NotEmpty
        private List<UUID> seatIds;
        @Min(0)
        private double finalAmount;
    }

    @Data
    public static class ConfirmBookingRequest {
        @NotNull
        private UUID bookingId;
        @NotNull
        private UUID paymentId;
    }

    public record BookingResponse(UUID id, UUID userId, UUID showId,
        List<UUID> seatIds, double finalAmount,
        Booking.BookingStatus status, String bookingRef, Instant createdAt) {}
}
