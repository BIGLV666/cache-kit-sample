CREATE TABLE IF NOT EXISTS t_user (
    user_id   BIGINT PRIMARY KEY,
    user_name VARCHAR(64) NOT NULL
);

INSERT INTO t_user (user_id, user_name) VALUES
    (1, 'alice'),
    (2, 'bob'),
    (3, 'carol')
ON DUPLICATE KEY UPDATE user_name = VALUES(user_name);
