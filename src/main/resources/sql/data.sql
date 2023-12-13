CREATE TABLE user
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     varchar(20) unique NOT NULL,
    password    varchar(64)        NOT NULL,
    nickname    varchar(20) unique NOT NULL,
    created_at  DATETIME,
    modified_at DATETIME
);

create table follow
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    following_id BIGINT NOT NULL,
    follower_id  BIGINT NOT NULL,
    follow_date  DATETIME,
    FOREIGN KEY (following_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (follower_id) REFERENCES user (id) ON DELETE CASCADE
);

drop table diary;
drop table likes;
drop table comment;


create table diary
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id       BIGINT       NOT NULL,
    title         varchar(100) NOT NULL,
    content       text,
    file_path     JSON,
    hashtags      JSON,
    like_count    BIGINT default 0,
    comment_count BIGINT default 0,
    created_at    DATETIME,
    modified_at   DATETIME,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
);

create table likes
(
    id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    diary_id BIGINT NOT NULL,
    user_id  BIGINT NOT NULL,
    FOREIGN KEY (diary_id) REFERENCES diary (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
);

create table comment
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    diary_id          BIGINT NOT NULL,
    user_id           BIGINT NOT NULL,
    parent_comment_id BIGINT,
    content           varchar(500),
    created_at        DATETIME,
    modified_at       DATETIME,
    FOREIGN KEY (diary_id) REFERENCES diary (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
);

drop table chat_room;
drop table message;

create table chat_room
(
    id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    FOREIGN KEY (user1_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES user (id) ON DELETE CASCADE
);

create table message
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    chat_room_id    BIGINT NOT NULL,
    sender_id  BIGINT NOT NULL,
    content    text,
    created_at DATETIME,
    FOREIGN KEY (chat_room_id) REFERENCES chat_room (id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES user (id) ON DELETE CASCADE
);

create table notification
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    receiver_id BIGINT      NOT NULL,
    alarm_type  varchar(50) NOT NULL,
    params      JSON,
    path        TEXT,
    created_at  DATETIME,
    checked_at  DATETIME,
    FOREIGN KEY (receiver_id) REFERENCES user (id) ON DELETE CASCADE
);