# --- !Ups
CREATE TABLE "purchase_history"
(
    "id"                 INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "cart"               INT       NOT NULL,
    "totalPrice"         DOUBLE    NOT NULL,
    "purchase_timestamp" TIMESTAMP NOT NULL,
    constraint fk_cart FOREIGN KEY (cart) REFERENCES cart (id)
);

INSERT INTO purchase_history(cart, totalPrice, purchase_timestamp)
VALUES ((SELECT ID from cart where user = (SELECT ID FROM user WHERE name = 'Anthony Rach')),
        1000.00, CURRENT_TIMESTAMP);

# --- !Downs
DROP TABLE "purchase_history";
