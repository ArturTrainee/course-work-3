CREATE TABLE users
(
    id       VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255) UNIQUE,
    bio      TEXT,
    image    VARCHAR(511)
);

CREATE TABLE articles
(
    id           VARCHAR(255) PRIMARY KEY,
    user_id      VARCHAR(255) REFERENCES users (id) ON DELETE CASCADE,
    slug         VARCHAR(255) NOT NULL UNIQUE,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    body         TEXT,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_published BOOLEAN      NOT NULL DEFAULT FALSE,
    total_views  INTEGER      NOT NULL DEFAULT 0 CHECK (total_views >= 0)
);

CREATE TABLE article_favorites
(
    article_id VARCHAR(255) REFERENCES articles (id) ON DELETE CASCADE,
    user_id    VARCHAR(255) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE follows
(
    user_id   VARCHAR(255) REFERENCES users (id) ON DELETE CASCADE,
    follow_id VARCHAR(255) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE tags
(
    id   VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE article_tags
(
    article_id VARCHAR(255) REFERENCES articles (id) ON DELETE CASCADE,
    tag_id     VARCHAR(255) REFERENCES tags (id) ON DELETE CASCADE
);

CREATE TABLE comments
(
    id         VARCHAR(255) PRIMARY KEY,
    body       TEXT,
    article_id VARCHAR(255) REFERENCES articles (id) ON DELETE CASCADE,
    user_id    VARCHAR(255) REFERENCES users (id) ON DELETE CASCADE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reactions
(
    id         VARCHAR(255) PRIMARY KEY,
    article_id VARCHAR(255) REFERENCES articles (id) ON DELETE CASCADE,
    user_id    VARCHAR(255) REFERENCES users(id) ON DELETE CASCADE,
    is_liked   BOOLEAN      NOT NULL
);

CREATE TABLE reading_histories
(
    article_id   VARCHAR(255) REFERENCES articles (id) ON DELETE CASCADE,
    user_id      VARCHAR(255) REFERENCES users (id) ON DELETE CASCADE,
    last_read_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
