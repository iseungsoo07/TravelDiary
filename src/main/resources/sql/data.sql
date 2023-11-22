CREATE TABLE user
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     varchar(20) unique NOT NULL,
    password    varchar(64)        NOT NULL,
    nickname    varchar(20) unique NOT NULL,
    created_at  DATETIME,
    modified_at DATETIME
);

create table follow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    following_id BIGINT NOT NULL,
    follower_id BIGINT NOT NULL,
    follow_date DATETIME,
    FOREIGN KEY (following_id) REFERENCES user(id),
    FOREIGN KEY (follower_id) REFERENCES user(id)
);

