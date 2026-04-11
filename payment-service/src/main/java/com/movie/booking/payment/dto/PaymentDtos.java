package com.movie.booking.payment.dto;
import com.movie.booking.payment.model.Payment;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

public class PaymentDtos {

    @Data
    public static class InitiatePaymentRequest {

        @NotNull
        private UUID bookingId;

        @NotNull
        private UUID userId;

        @Min(1)
        private double amount;

        @NotBlank
        private String currency;

        @NotNull
        private Payment.PaymentMethod method;
    }

    @Data public static class ProcessPaymentRequest {

        @NotNull
        private UUID paymentId;

        private String cardLastFour;

        private String upiId;

        private String otp;
    }

    @Data
    public static class RefundRequest {
        @NotNull
        private UUID paymentId;

        @NotBlank
        private String reason;
    }

    public record PaymentResponse(UUID id, UUID bookingId, UUID userId, double amount,
        String currency, Payment.PaymentMethod method, Payment.PaymentStatus status,
        String transactionId, String failureReason, Instant createdAt, Instant processedAt) {}
}
