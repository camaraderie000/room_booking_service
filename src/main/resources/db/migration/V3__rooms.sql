CREATE TABLE rooms (
    id          UUID PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,
    capacity    INT  NOT NULL CHECK (capacity > 0),
    description TEXT
);