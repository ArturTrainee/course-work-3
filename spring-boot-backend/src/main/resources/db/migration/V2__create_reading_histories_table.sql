CREATE TABLE reading_histories (
    article_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    last_read_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(article_id, user_id)
);