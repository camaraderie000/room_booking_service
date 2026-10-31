CREATE TABLE slots (
    id       UUID PRIMARY KEY,
    room_id  UUID        NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    start_at TIMESTAMPTZ NOT NULL,
    end_at   TIMESTAMPTZ NOT NULL,
    CHECK (start_at < end_at),
    UNIQUE (room_id, start_at)
);