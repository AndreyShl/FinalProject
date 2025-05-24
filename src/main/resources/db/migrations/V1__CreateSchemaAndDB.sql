CREATE SCHEMA `shop` ;
CREATE TABLE `shop`.`users` (
                                `ID` INT NOT NULL AUTO_INCREMENT,
                                `userName` VARCHAR(45) NOT NULL,
                                `login` VARCHAR(45) NOT NULL,
                                `password` VARCHAR(45) NOT NULL,
                                `phoneNumber` VARCHAR(45) NULL,
                                `role` VARCHAR(45) NOT NULL,
                                `balance` DECIMAL(10,2) NULL,

                                PRIMARY KEY (`ID`));
CREATE TABLE `shop`.`category` (
                                   `ID` INT NOT NULL AUTO_INCREMENT,
                                   `category_name` VARCHAR(255) NULL,
                                   PRIMARY KEY (`ID`));
CREATE TABLE `shop`.`products` (
                                   `ID` INT NOT NULL AUTO_INCREMENT,
                                   `name` VARCHAR(255) NULL,
                                   `category_id` INT NULL,
                                   `price` DECIMAL(10,2) NULL,
                                   `discount` DECIMAL(10,2) NULL,
                                   `remains` INT NULL,
                                   `description` VARCHAR(255) NULL,
                                   PRIMARY KEY (`ID`));
CREATE TABLE `shop`.`basket` (
                                 `ID` INT NOT NULL AUTO_INCREMENT,
                                 `user_id` INT NULL,
                                 `product_id` INT NULL,
                                 `amount` DECIMAL(10,2) NULL,
                                 PRIMARY KEY (`ID`));

CREATE TABLE `shop`.`favourites` (
                                     `ID` INT NOT NULL AUTO_INCREMENT,
                                     `user_id` INT NOT NULL,
                                     `product_id` INT NOT NULL,
                                     PRIMARY KEY (`ID`));
CREATE TABLE `shop`.`orders` (
                                 `ID` INT NOT NULL AUTO_INCREMENT,
                                 `user_id` INT NOT NULL,
                                 `product_id` INT NOT NULL,
                                 `payment` VARCHAR(45) NOT NULL,
                                 `delivery` VARCHAR(45) NOT NULL,
                                 PRIMARY KEY (`ID`));
CREATE TABLE `shop`.`products_orders` (
                                          `ID` INT NOT NULL AUTO_INCREMENT,
                                          `user_id` INT NOT NULL,
                                          `product_id` INT NOT NULL,
                                          `order_id` INT NOT NULL,
                                          PRIMARY KEY (`ID`));
-- CREATE TABLE `shop`.`history` (
--                                   `ID` INT NOT NULL AUTO_INCREMENT,
--                                   `user_id` INT NOT NULL,
--                                   `product_id` INT NOT NULL,
--                                   `amount` DECIMAL(10,2) NOT NULL,
--                                   PRIMARY KEY (`ID`));
alter table orders add constraint FOREIGN KEY (user_id) REFERENCES users(ID);
alter table orders add constraint FOREIGN KEY (product_id) REFERENCES products(ID);
alter table products_orders add constraint FOREIGN KEY (user_id) REFERENCES users(ID);
alter table products_orders add constraint FOREIGN KEY (product_id) REFERENCES products(ID);
alter table products_orders add constraint FOREIGN KEY (order_id) REFERENCES orders(ID);

-- alter table history add constraint FOREIGN KEY (user_id) REFERENCES users(ID)
-- alter table history add constraint FOREIGN KEY (product_id) REFERENCES products(ID)
alter table basket add constraint FOREIGN KEY (user_id) REFERENCES users(ID);
alter table basket add constraint FOREIGN KEY (product_id) REFERENCES products(ID);
alter table favourites add constraint FOREIGN KEY (user_id) REFERENCES users(ID);
alter table favourites add constraint FOREIGN KEY (product_id) REFERENCES products(ID);
alter table products add constraint FOREIGN KEY (category_id) REFERENCES category(ID);

ALTER TABLE `shop`.`favourites`
    ADD COLUMN `status` INT NULL AFTER `product_id`;

ALTER TABLE `shop`.`orders`
    CHANGE COLUMN `delivery` `order_status` DECIMAL(10,2) NOT NULL ;

ALTER TABLE `shop`.`category`
    ADD COLUMN `status` INT NULL AFTER `category_name`;
ALTER TABLE `shop`.`orders`
    CHANGE COLUMN `payment` `price` DECIMAL(10,2) NOT NULL ,
    CHANGE COLUMN `order_status` `order_status` VARCHAR(45) NOT NULL ;
ALTER TABLE `shop`.`favourites`
    CHANGE COLUMN `status` `status` VARCHAR(45) NULL DEFAULT NULL ;
ALTER TABLE `shop`.`category`
    CHANGE COLUMN `status` `status` VARCHAR(255) NULL DEFAULT NULL ;
ALTER TABLE `shop`.`users`
    ADD COLUMN `status` VARCHAR(45) NULL DEFAULT 'ACTIVE' AFTER `balance`;
ALTER TABLE `shop`.`category`
    CHANGE COLUMN `status` `status` VARCHAR(255) NULL DEFAULT 'ACTIVE' ;
