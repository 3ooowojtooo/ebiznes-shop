# --- !Ups

CREATE TABLE "category" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "name" VARCHAR NOT NULL
);

CREATE TABLE "product" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "name" VARCHAR NOT NULL,
 "description" TEXT NOT NULL,
 "category" INT NOT NULL,
 "price" DOUBLE NOT NULL,
 FOREIGN KEY(category) references category(id)
);

CREATE TABLE "user" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "name" VARCHAR NOT NULL,
 "age" INT NOT NULL
);

# --- !Downs

DROP TABLE "user";
DROP TABLE "product";
DROP TABLE "category";