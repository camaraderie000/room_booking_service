CREATE TABLE room_schedules (
    room_id      UUID PRIMARY KEY REFERENCES rooms(id) ON DELETE CASCADE,
    days_of_week TEXT NOT NULL,
    open_time    TIME NOT NULL,
    close_time   TIME NOT NULL,
    slot_minutes INT  NOT NULL CHECK (slot_minutes > 0),
    timezone     TEXT NOT NULL DEFAULT 'UTC',
    CHECK (open_time < close_time)
);