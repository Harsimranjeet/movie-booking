package com.movie.booking.payment.service;
import com.movie.booking.payment.dto.PaymentDtos.*;
import com.movie.booking.payment.exception.BadRequestException;
import com.movie.booking.payment.exception.ResourceNotFoundException;
import com.movie.booking.payment.model.Payment;
import com.movie.booking.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repo;

    public PaymentResponse getById(UUID id) {
        log.debug("Fetching payment by id={}", id);
        return toDto(find(id));
    }

    public PaymentResponse getByBooking(UUID bid) {
        log.debug("Fetching payment for bookingId={}", bid);
        return toDto(repo.findByBookingId(bid).orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking: " + bid)));
    }

    public List<PaymentResponse> getByUser(UUID uid) {
        log.debug("Fetching payments for userId={}", uid);
        List<PaymentResponse> payments = repo.findByUserId(uid).stream().map(this::toDto).toList();
        log.debug("Found {} payments for userId={}", payments.size(), uid);
        return payments;
    }

    @Transactional
    public PaymentResponse initiate(InitiatePaymentRequest req) {
        log.info("Initiating payment: bookingId={}, userId={}, amount={}, method={}", req.getBookingId(), req.getUserId(), req.getAmount(), req.getMethod());
        if (repo.findByBookingId(req.getBookingId()).isPresent()) {
            log.warn("Payment initiation failed — already exists for bookingId={}", req.getBookingId());
            throw new BadRequestException("Payment already initiated for booking: " + req.getBookingId());
        }
        Payment p = Payment.builder()
            .bookingId(req.getBookingId()).userId(req.getUserId())
            .amount(req.getAmount()).currency(req.getCurrency())
            .method(req.getMethod()).status(Payment.PaymentStatus.PENDING).build();
        PaymentResponse saved = toDto(repo.save(p));
        log.info("Payment initiated: id={}, bookingId={}", saved.getId(), saved.getBookingId());
        return saved;
    }

    /**
     * Demo payment processor — simulates gateway response.
     * In production: integrate Razorpay/Stripe/PayU here.
     */
    @Transactional
    public PaymentResponse process(ProcessPaymentRequest req) {
        Payment p = find(req.getPaymentId());
        if (p.getStatus() != Payment.PaymentStatus.PENDING &&
            p.getStatus() != Payment.PaymentStatus.INITIATED)
            throw new BadRequestException("Payment cannot be processed in status: " + p.getStatus());

        // Demo: always succeed for amounts < 10000, fail otherwise
        boolean success = p.getAmount() < 10000;

        if (success) {
            p.setStatus(Payment.PaymentStatus.SUCCESS);
            p.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            p.setGatewayResponse("Payment successful (demo)");
        } else {
            p.setStatus(Payment.PaymentStatus.FAILED);
            p.setFailureReason("Demo: amount exceeds 10000 limit");
        }
        p.setProcessedAt(Instant.now());

        Payment saved = repo.save(p);
        log.info("Payment {} processed: status={}, txn={}", p.getId(), p.getStatus(), p.getTransactionId());
        return toDto(saved);
    }

    @Transactional
    public PaymentResponse refund(RefundRequest req) {
        log.info("Refund requested: paymentId={}, reason='{}'", req.getPaymentId(), req.getReason());
        Payment p = find(req.getPaymentId());
        if (p.getStatus() != Payment.PaymentStatus.SUCCESS) {
            log.warn("Refund failed — payment not in SUCCESS state: paymentId={}, currentStatus={}", req.getPaymentId(), p.getStatus());
            throw new BadRequestException("Can only refund successful payments");
        }
        p.setStatus(Payment.PaymentStatus.REFUNDED);
        p.setGatewayResponse("Refund processed: " + req.getReason());
        PaymentResponse refunded = toDto(repo.save(p));
        log.info("Refund processed: paymentId={}, bookingId={}", refunded.getId(), refunded.getBookingId());
        return refunded;
    }

    private Payment find(UUID id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
    }
    private PaymentResponse toDto(Payment p) {
        return new PaymentResponse(p.getId(),p.getBookingId(),p.getUserId(),p.getAmount(),
            p.getCurrency(),p.getMethod(),p.getStatus(),p.getTransactionId(),
            p.getFailureReason(),p.getCreatedAt(),p.getProcessedAt());
    }
}
