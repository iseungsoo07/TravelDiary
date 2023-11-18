CREATE TABLE user
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     varchar(20) unique NOT NULL,
    password    varchar(64)        NOT NULL,
    nickname    varchar(20) unique NOT NULL,
    created_at  DATETIME,
    modified_at DATETIME
);

