ALTER TABLE `shop`.`orders`
DROP FOREIGN KEY `orders_ibfk_2`;
ALTER TABLE `shop`.`orders`
DROP INDEX `product_id` ;
ALTER TABLE `shop`.`orders`
DROP COLUMN `product_id`;