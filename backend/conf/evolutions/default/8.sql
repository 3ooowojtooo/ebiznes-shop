# --- !Ups
CREATE TABLE "payment_method"
(
    "id"   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "user" INT     NOT NULL,
    "name" TEXT    NOT NULL,
    constraint fk_user FOREIGN KEY (user) REFERENCES user (id)
);

INSERT INTO payment_method(user, name)
VALUES ((SELECT ID FROM user WHERE email = 'raskony@gmail.com'), 'Credit Card');

# --- !Downs
DROP TABLE "payment_method";
