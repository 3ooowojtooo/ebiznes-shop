# --- !Ups
CREATE TABLE "purchase_history"
(
    "id"                 INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
    "cart"               INT       NOT NULL,
    "payment_method"     INT       NOT NULL,
    "address"            INT       NOT NULL,
    "totalPrice"         DOUBLE    NOT NULL,
    "purchase_timestamp" TIMESTAMP NOT NULL,
    constraint fk_cart FOREIGN KEY (cart) REFERENCES cart (id),
    constraint fk_payment_method FOREIGN KEY (payment_method) REFERENCES payment_method (id),
    constraint fk_address FOREIGN KEY (address) REFERENCES user_address (id)
);

INSERT INTO purchase_history(cart, payment_method, address, totalPrice, purchase_timestamp)
VALUES ((SELECT ID from cart where user = (SELECT ID FROM user WHERE email = 'raskony@gmail.com')),
        (SELECT ID from payment_method where name = 'Credit Card'),
        (SELECT ID from user_address where user = (SELECT ID FROM user WHERE email = 'raskony@gmail.com')),
        1000.00, CURRENT_TIMESTAMP);

# --- !Downs
DROP TABLE "purchase_history";

