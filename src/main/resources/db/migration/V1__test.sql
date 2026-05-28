CREATE TABLE IF NOT EXISTS queue (
    id BIGSERIAL PRIMARY KEY,
    queue_name TEXT NOT NULL,
    chat_id BIGINT,
    start_date DATE,
    start_time TIME,
    end_time TIME
);


CREATE TABLE IF NOT EXISTS member (
    id BIGSERIAL PRIMARY KEY,
    user_name TEXT NOT NULL,
    position INTEGER NOT NULL,
    queue_id BIGINT NOT NULL,
    CONSTRAINT fk_queue
        FOREIGN KEY (queue_id)
            REFERENCES queue(id)
            ON DELETE CASCADE
);