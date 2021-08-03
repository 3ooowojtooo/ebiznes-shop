# --- !Ups
CREATE TABLE "cart"
(
    "id"           INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "created_time" TIMESTAMP NOT NULL,
    "user"         INT       NOT NULL,
    "purchased"    BOOLEAN   NOT NULL,
    FOREIGN KEY (user) REFERENCES user (id)
);

INSERT INTO cart(created_time, user, purchased)
VALUES (CURRENT_TIMESTAMP, (SELECT ID FROM user WHERE name = 'Anthony Rach'), false);

# --- !Downs
DROP TABLE "cart";
