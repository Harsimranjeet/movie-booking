# Movie Ticket Booking Platform

10 Spring Boot 3.2 microservices · Java 21 · H2 · JWT · File-based seat locking

## Services

| Service            | Port | Responsibility                              |
|--------------------|------|---------------------------------------------|
| discovery-server   | 8761 | Eureka service registry                     |
| api-gateway        | 8080 | JWT validation, routing, CORS               |
| user-service       | 8081 | Signup, login, profile management           |
| movie-service      | 8082 | Movies, genres, languages                   |
| theatre-service    | 8083 | Theatres, screens                           |
| show-service       | 8084 | Show timings, scheduling                    |
| seat-service       | 8085 | Seat availability, file-based locking       |
| booking-service    | 8086 | Ticket booking lifecycle                    |
| payment-service    | 8087 | Payment processing (demo gateway)           |
| offer-service      | 8088 | Discount codes, pricing rules               |

---

## Run with Docker (recommended)

```bash
docker-compose up --build
```

All services start automatically. Discovery server is the dependency anchor — others wait for it.

---

## Run locally (H2 in-memory, no Docker)

```bash
# Terminal per service
cd discovery-server && mvn spring-boot:run
cd api-gateway      && mvn spring-boot:run
cd user-service     && mvn spring-boot:run
# ... repeat for all services
```

H2 consoles available at `http://localhost:{port}/h2-console`

---

## All APIs (via Gateway on :8080)

### User Service — Auth

```bash
# Signup
POST /api/v1/auth/signup
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "Password1",
  "phone": "+919876543210",
  "role": "CUSTOMER"
}

# Login
POST /api/v1/auth/login
{ "identifier": "john@example.com", "password": "Password1" }
```

### User Service — Profile

```bash
GET    /api/v1/users/me                          # own profile
PUT    /api/v1/users/me                          # update profile
PUT    /api/v1/users/me/password                 # change password
GET    /api/v1/users/{id}                        # admin only
GET    /api/v1/users                             # admin only
DELETE /api/v1/users/{id}                        # admin only
```

### Movie Service

```bash
GET    /api/v1/movies                            # all active movies
GET    /api/v1/movies/{id}                       # single movie
GET    /api/v1/movies/search?title=Avengers      # search
GET    /api/v1/movies/language/Hindi             # filter by language
GET    /api/v1/movies/genre/Action               # filter by genre
POST   /api/v1/movies                            # create (ADMIN)
PUT    /api/v1/movies/{id}                       # update (ADMIN)
DELETE /api/v1/movies/{id}                       # soft delete (ADMIN)

GET    /api/v1/movies/genres                     # all genres
POST   /api/v1/movies/genres                     # create genre (ADMIN)
DELETE /api/v1/movies/genres/{id}                # delete genre (ADMIN)

GET    /api/v1/movies/languages                  # all languages
POST   /api/v1/movies/languages                  # create language (ADMIN)
DELETE /api/v1/movies/languages/{id}             # delete language (ADMIN)
```

### Theatre Service

```bash
GET    /api/v1/theatres                          # all theatres
GET    /api/v1/theatres/{id}                     # single theatre
GET    /api/v1/theatres/city/{city}              # by city
POST   /api/v1/theatres                          # create (ADMIN/PARTNER)
PUT    /api/v1/theatres/{id}                     # update (ADMIN/PARTNER)
DELETE /api/v1/theatres/{id}                     # delete (ADMIN)

GET    /api/v1/theatres/{theatreId}/screens      # screens in theatre
POST   /api/v1/theatres/{theatreId}/screens      # add screen (ADMIN/PARTNER)
DELETE /api/v1/theatres/screens/{screenId}       # remove screen
```

### Show Service

```bash
GET    /api/v1/shows/{id}                        # single show
GET    /api/v1/shows/movie/{movieId}?date=YYYY-MM-DD   # shows for movie
GET    /api/v1/shows/theatre/{theatreId}?date=...      # shows at theatre
GET    /api/v1/shows/search?movieId=...&theatreId=...&date=...
GET    /api/v1/shows/date/YYYY-MM-DD             # all open shows on date
POST   /api/v1/shows                             # create show (ADMIN/PARTNER)
PUT    /api/v1/shows/{id}                        # update show
DELETE /api/v1/shows/{id}                        # cancel show
```

### Seat Service

```bash
GET  /api/v1/seats/show/{showId}                 # all seats for show
GET  /api/v1/seats/show/{showId}/available       # available seats only
GET  /api/v1/seats/show/{showId}/category/{cat}  # seats by category
POST /api/v1/seats/show/{showId}/bulk?screenId=  # bulk create seats

POST /api/v1/seats/reserve
{
  "seatIds": ["uuid1", "uuid2"],
  "bookingId": "uuid",
  "lockMinutes": 10
}

POST /api/v1/seats/confirm/{bookingId}
POST /api/v1/seats/release/{bookingId}
```

### Booking Service

```bash
GET  /api/v1/bookings/{id}
GET  /api/v1/bookings/ref/{bookingRef}           # by booking reference
GET  /api/v1/bookings/my                         # own bookings (JWT userId)
GET  /api/v1/bookings/show/{showId}              # admin/partner only

POST /api/v1/bookings
{
  "showId": "uuid",
  "theatreId": "uuid",
  "movieId": "uuid",
  "seatIds": ["uuid1","uuid2"],
  "ticketCount": 2,
  "totalAmount": 500.00,
  "offerCode": "SAVE20"
}

POST /api/v1/bookings/confirm
{ "bookingId": "uuid", "paymentId": "uuid" }

POST /api/v1/bookings/{id}/cancel
```

### Payment Service

```bash
GET  /api/v1/payments/{id}
GET  /api/v1/payments/booking/{bookingId}
GET  /api/v1/payments/user/{userId}

POST /api/v1/payments/initiate
{
  "bookingId": "uuid",
  "userId": "uuid",
  "amount": 500.00,
  "currency": "INR",
  "method": "UPI"
}

POST /api/v1/payments/process
{ "paymentId": "uuid", "upiId": "john@upi" }

POST /api/v1/payments/refund
{ "paymentId": "uuid", "reason": "Cancelled by user" }
```

### Offer Service

```bash
GET  /api/v1/offers                              # active offers
GET  /api/v1/offers/{id}

POST /api/v1/offers                              # create (ADMIN)
{
  "code": "SAVE20",
  "description": "20% off on all bookings",
  "discountType": "PERCENTAGE",
  "discountValue": 20,
  "maxDiscount": 200,
  "validFrom": "2025-01-01",
  "validTo": "2025-12-31"
}

POST /api/v1/offers/apply                        # calculate discount
{
  "code": "MATINEE20",
  "ticketCount": 2,
  "totalAmount": 500.00,
  "showTime": "14:30"
}

DELETE /api/v1/offers/{id}                       # deactivate (ADMIN)
```

---

## Typical booking flow

```
1. POST /api/v1/auth/signup           → get JWT
2. GET  /api/v1/movies                → pick movie
3. GET  /api/v1/shows/movie/{id}?date → pick show
4. GET  /api/v1/seats/show/{id}/available → pick seats
5. POST /api/v1/offers/apply          → optional: get discount
6. POST /api/v1/bookings              → create booking (PENDING)
7. POST /api/v1/seats/reserve         → lock seats for 10 min
8. POST /api/v1/payments/initiate     → initiate payment
9. POST /api/v1/payments/process      → process payment
10. POST /api/v1/bookings/confirm     → confirm booking
11. POST /api/v1/seats/confirm/{id}   → mark seats BOOKED
```

---

## Authorization header

All protected endpoints require:
```
Authorization: Bearer <token_from_login_response>
```

## Roles

- `CUSTOMER` — browse, book, view own data
- `ADMIN` — full access including create/delete movies, theatres, offers
- `THEATRE_PARTNER` — manage own theatres, screens, shows
