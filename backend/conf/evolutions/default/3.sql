# --- !Ups
CREATE TABLE "stock"
(
    "id"      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "product" INT     NOT NULL,
    "amount"  INT     NOT NULL,
    FOREIGN KEY (product) REFERENCES product (id)
);

INSERT INTO stock(product, amount)
VALUES ((SELECT id FROM product WHERE name = 'sofa'), 100);
INSERT INTO stock(product, amount)
VALUES ((SELECT id FROM product WHERE name = 'armchair'), 61);
INSERT INTO stock(product, amount)
VALUES ((SELECT id FROM product WHERE name = 'swing'), 12);
INSERT INTO stock(product, amount)
VALUES ((SELECT id FROM product WHERE name = 'grill'), 200);
INSERT INTO stock(product, amount)
VALUES ((SELECT id FROM product WHERE name = 'screen'), 1200);
INSERT INTO stock(product, amount)
VALUES ((SELECT id FROM product WHERE name = 'smartphone'), 2010);
INSERT INTO stock(product, amount)
VALUES ((SELECT id FROM product WHERE name = 'Subnautica'), 350);
INSERT INTO stock(product, amount)
VALUES ((SELECT id FROM product WHERE name = 'Subnautica: Below Zero'), 400);

# --- !Downs
DROP TABLE "stock";
