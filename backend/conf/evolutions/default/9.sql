# --- !Ups
CREATE TABLE "delivery"
(
    "id"                 INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "cart"               INT       NOT NULL,
    "delivery_timestamp" TIMESTAMP NOT NULL,
    "delivered"          BOOLEAN   NOT NULL,
    constraint fk_cart FOREIGN KEY (cart) REFERENCES cart (id)
);

INSERT INTO delivery(cart, delivery_timestamp, delivered)
VALUES ((SELECT ID from cart WHERE user = (SELECT ID FROM user WHERE email = 'raskony@gmail.com')),
        CURRENT_TIMESTAMP, true);

# --- !Downs
DROP TABLE "delivery";
