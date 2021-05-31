# --- !Ups
CREATE TABLE "cart_item" (
                        "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        "product" INT NOT NULL,
                        "amount" INT NOT NULL,
                        "cart" INT NOT NULL,
                        constraint fk_product FOREIGN KEY (product) REFERENCES product(id),
                        constraint fk_cart FOREIGN KEY (cart) REFERENCES cart(id)
);

INSERT INTO cart_item(product, amount, cart) VALUES ((SELECT ID from product where name = 'grill'), 1,
                                                     (SELECT ID from cart where user = (SELECT ID FROM user WHERE name = 'Anthony Rach')));

# --- !Downs
DROP TABLE "cart_item";
