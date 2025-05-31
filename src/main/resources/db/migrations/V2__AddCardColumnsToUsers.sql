ALTER TABLE `shop`.`users`
    ADD COLUMN `card_number` VARCHAR(255) NULL AFTER `status`,
    ADD COLUMN `card_holder` VARCHAR(255) NULL AFTER `card_number`,
    ADD COLUMN `card_expiry` VARCHAR(255) NULL AFTER `card_holder`,
    ADD COLUMN `card_cvv` VARCHAR(255) NULL AFTER `card_expiry`;