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

select file_path
from diary;