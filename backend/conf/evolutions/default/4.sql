# --- !Ups
CREATE TABLE "user_address" (
                        "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        "street" VARCHAR NOT NULL,
                        "city" VARCHAR NOT NULL,
                        "zipcode" VARCHAR(5) NOT NULL,
                        "user" INT NOT NULL,
                        FOREIGN KEY(user) REFERENCES user(id)
);

INSERT INTO user_address(street, city, zipcode, user) VALUES ('ul. Zamkowa 3', 'Kraków', '11-920',
                                                              (SELECT ID FROM user WHERE name = 'Anthony Rach'));

# --- !Downs
DROP TABLE "user_address";
