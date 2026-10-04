CREATE TABLE users (
                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(1000) NOT NULL,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE events (
                        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        place VARCHAR(255) NOT NULL,
                        starts_at TIMESTAMPTZ NOT NULL,
                        created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE seats (
                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,

                       seat_column VARCHAR(2) NOT NULL,
                       seat_row INTEGER NOT NULL,
                       price NUMERIC(10, 2) NOT NULL,

                       created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT uq_event_seat
                           UNIQUE (event_id, seat_column, seat_row),

                       CONSTRAINT chk_seat_row_positive
                           CHECK (seat_row > 0),

                       CONSTRAINT chk_price_non_negative
                           CHECK (price >= 0)
);

CREATE TABLE reservations (
                              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                              user_id BIGINT NOT NULL REFERENCES users(id),
                              seat_id BIGINT NOT NULL REFERENCES seats(id),

                              status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

                              created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT chk_reservation_status
                                  CHECK (status IN ('PENDING', 'PAID', 'CANCELLED'))
);

CREATE UNIQUE INDEX uq_active_reservation_per_seat
ON reservations(seat_id)
WHERE status IN ('PENDING', 'PAID');