package com.movie.booking.user.config;

import com.movie.booking.user.model.User;
import com.movie.booking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository       userRepo;
    private final BCryptPasswordEncoder encoder;

    // ── Fixed User UUIDs (referenced by booking-service seed) ─────────────────
    static final UUID U1 = UUID.fromString("50000000-0000-0000-0000-000000000001"); // admin
    static final UUID U2 = UUID.fromString("50000000-0000-0000-0000-000000000002"); // theatre partner PVR
    static final UUID U3 = UUID.fromString("50000000-0000-0000-0000-000000000003"); // theatre partner INOX
    static final UUID U4 = UUID.fromString("50000000-0000-0000-0000-000000000004"); // customer Rahul
    static final UUID U5 = UUID.fromString("50000000-0000-0000-0000-000000000005"); // customer Priya
    static final UUID U6 = UUID.fromString("50000000-0000-0000-0000-000000000006"); // customer Arjun
    static final UUID U7 = UUID.fromString("50000000-0000-0000-0000-000000000007"); // customer Ananya
    static final UUID U8 = UUID.fromString("50000000-0000-0000-0000-000000000008"); // customer Vikram

    @Override
    public void run(ApplicationArguments args) {
        if (userRepo.count() > 0) return;

        String adminPwd    = encoder.encode("Admin@123");
        String partnerPwd  = encoder.encode("Partner@123");
        String customerPwd = encoder.encode("Customer@123");

        user(U1, "Admin User",       "admin@movie.com",         adminPwd,    "+911111111111", User.Role.ADMIN);
        user(U2, "PVR Partner",      "pvr@theatre.com",         partnerPwd,  "+912222222222", User.Role.THEATRE_PARTNER);
        user(U3, "INOX Partner",     "inox@theatre.com",        partnerPwd,  "+913333333333", User.Role.THEATRE_PARTNER);
        user(U4, "Rahul Sharma",     "rahul.sharma@gmail.com",  customerPwd, "+914444444444", User.Role.CUSTOMER);
        user(U5, "Priya Patel",      "priya.patel@gmail.com",   customerPwd, "+915555555555", User.Role.CUSTOMER);
        user(U6, "Arjun Singh",      "arjun.singh@gmail.com",   customerPwd, "+916666666666", User.Role.CUSTOMER);
        user(U7, "Ananya Kumar",     "ananya.kumar@gmail.com",  customerPwd, "+917777777777", User.Role.CUSTOMER);
        user(U8, "Vikram Reddy",     "vikram.reddy@gmail.com",  customerPwd, "+918888888888", User.Role.CUSTOMER);

        log.info("User-service seed data loaded. Credentials: admin@movie.com / Admin@123  |  rahul.sharma@gmail.com / Customer@123");
    }

    private void user(UUID id, String fullName, String email, String pwdHash, String phone, User.Role role) {
        User u = User.builder().fullName(fullName).email(email).passwordHash(pwdHash)
            .phone(phone).role(role).build();
        u.setId(id);
        userRepo.save(u);
    }
}
