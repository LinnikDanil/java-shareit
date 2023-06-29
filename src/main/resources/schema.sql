drop table IF EXISTS users cascade;
drop table IF EXISTS items cascade;
drop table IF EXISTS bookings cascade;
drop table IF EXISTS comments cascade;
drop table IF EXISTS item_requests cascade;

create TABLE IF NOT EXISTS users
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    identity
    not
    null
    PRIMARY
    KEY,
    name
    varchar
(
    255
) not null,
    email varchar
(
    512
) not null unique
    );

create TABLE IF NOT EXISTS items
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL,
    description TEXT NOT NULL,
    available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL REFERENCES users
(
    id
),
    request_id BIGINT
    );

create TABLE IF NOT EXISTS bookings
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL
    PRIMARY
    KEY,
    start_date
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    end_date
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    item_id
    BIGINT
    NOT
    NULL
    REFERENCES
    items
(
    id
),
    booker_id BIGINT NOT NULL REFERENCES users
(
    id
),
    status VARCHAR
(
    20
) NOT NULL,
    CHECK
(
    end_date >
    start_date
)
    );

create TABLE IF NOT EXISTS comments
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL
    PRIMARY
    KEY,
    text
    TEXT
    NOT
    NULL,
    item_id
    BIGINT
    NOT
    NULL
    REFERENCES
    items
(
    id
),
    author_id BIGINT NOT NULL REFERENCES users
(
    id
),
    created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL
    );

create TABLE IF NOT EXISTS item_requests
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL
    PRIMARY
    KEY,
    user_id
    BIGINT
    NOT
    NULL
    REFERENCES
    users
(
    id
),
    description TEXT NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL
    );