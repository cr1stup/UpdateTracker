CREATE TABLE IF NOT EXISTS chat
(
    id BIGINT NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS link
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    url             TEXT UNIQUE NOT NULL,
    description     TEXT NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE,
    last_checked_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS chat_link
(
    id       BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    chat_id  BIGINT NOT NULL REFERENCES chat (id) ON DELETE CASCADE,
    link_id  BIGINT NOT NULL REFERENCES link (id) ON DELETE CASCADE,
    UNIQUE (chat_id, link_id)
);

CREATE TABLE IF NOT EXISTS tag
(
    id   BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS chat_link_tag
(
    chat_link_id BIGINT NOT NULL REFERENCES chat_link (id) ON DELETE CASCADE,
    tag_id       BIGINT NOT NULL REFERENCES tag (id) ON DELETE CASCADE,
    PRIMARY KEY (chat_link_id, tag_id)
);

CREATE TABLE IF NOT EXISTS filter
(
    id   BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS chat_link_filter
(
    chat_link_id BIGINT NOT NULL REFERENCES chat_link (id) ON DELETE CASCADE,
    filter_id    BIGINT NOT NULL REFERENCES filter (id) ON DELETE CASCADE,
    PRIMARY KEY (chat_link_id, filter_id)
);

CREATE TABLE IF NOT EXISTS mode
(
    id   BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS chat_mode
(
    chat_id BIGINT PRIMARY KEY REFERENCES chat (id) ON DELETE CASCADE,
    mode_id BIGINT NOT NULL REFERENCES mode (id),
    time    TIME
);
