# --- !Ups

INSERT INTO "category"(name) VALUES ('furniture');
INSERT INTO "category"(name) VALUES ('garden');
INSERT INTO "category"(name) VALUES ('electronics');
INSERT INTO "category"(name) VALUES ('games');

INSERT INTO "product"(name, description, category) VALUES ('sofa', 'A comfortable piece of furniture for long meetings',
                                                           (SELECT id FROM category WHERE name = 'furniture'));
INSERT INTO "product"(name, description, category) VALUES ('armchair', 'The perfect gift for your grandparents',
                                                           (SELECT id FROM category WHERE name = 'furniture'));
INSERT INTO "product"(name, description, category) VALUES ('swing', 'A toy for your kids',
                                                           (SELECT id FROM category WHERE name = 'garden'));
INSERT INTO "product"(name, description, category) VALUES ('grill', 'A way to make delicious sausages',
                                                           (SELECT id FROM category WHERE name = 'garden'));
INSERT INTO "product"(name, description, category) VALUES ('screen', 'High resolution screen with excellent quality',
                                                           (SELECT id FROM category WHERE name = 'electronics'));
INSERT INTO "product"(name, description, category) VALUES ('smartphone', 'IPhone with bunch of brand new features',
                                                           (SELECT id FROM category WHERE name = 'electronics'));
INSERT INTO "product"(name, description, category) VALUES ('Subnautica', 'Amazing survival game settled in an underwater world',
                                                           (SELECT id FROM category WHERE name = 'games'));
INSERT INTO "product"(name, description, category) VALUES ('Subnautica: Below Zero', 'Brand new addition to Subnautica',
                                                           (SELECT id FROM category WHERE name = 'games'));

INSERT INTO "user"(name, age) VALUES ('Anthony Rach', 32);

# --- !Downs

DELETE FROM user WHERE name = 'Anthony Rach';

DELETE FROM product WHERE name = 'Subnautica: Below Zero';
DELETE FROM product WHERE name = 'Subnautica';
DELETE FROM product WHERE name = 'smartphone';
DELETE FROM product WHERE name = 'screen';
DELETE FROM product WHERE name = 'grill';
DELETE FROM product WHERE name = 'swing';
DELETE FROM product WHERE name = 'armchair';
DELETE FROM product WHERE name = 'sofa';

DELETE FROM category WHERE name = 'games';
DELETE FROM category WHERE name = 'electronics';
DELETE FROM category WHERE name = 'garden';
DELETE FROM category WHERE name = 'furniture';