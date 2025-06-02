CREATE TABLE IF NOT EXISTS shop.product_images (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL,
    image_path VARCHAR(255) NOT NULL,
    is_main BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (product_id) REFERENCES shop.products(id) ON DELETE CASCADE
);