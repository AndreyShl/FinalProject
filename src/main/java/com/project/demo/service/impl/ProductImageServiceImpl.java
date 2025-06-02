package com.project.demo.service.impl;

import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.ProductImage;
import com.project.demo.model.repository.ProductImageRepository;
import com.project.demo.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductImageServiceImpl implements ProductImageService {
    
    @Value("${app.upload.dir:${user.home}/uploads}")
    private String uploadDir;
    
    @Autowired
    private ProductImageRepository productImageRepository;
    
    @Override
    public List<ProductImage> saveProductImages(Product product, MultipartFile[] images) throws IOException {
        List<ProductImage> savedImages = new ArrayList<>();
        boolean isFirst = product.getImages().isEmpty();
        
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                // Create directory if it doesn't exist
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                // Generate unique filename
                String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);
                
                // Save file
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                // Create database record
                ProductImage productImage = new ProductImage();
                productImage.setProduct(product);
                productImage.setImagePath(filePath.toString());
                productImage.setIsMain(isFirst); // First image becomes main
                
                ProductImage savedImage = productImageRepository.save(productImage);
                savedImages.add(savedImage);
                
                isFirst = false;
            }
        }
        
        return savedImages;
    }
    
    @Override
    public List<ProductImage> getProductImagesByProduct(Product product) {
        return productImageRepository.findByProduct(product);
    }
    
    @Override
    public ProductImage getMainProductImage(Product product) {
        return productImageRepository.findByProductAndIsMainTrue(product);
    }
    
    @Override
    public boolean deleteProductImage(Integer imageId) {
        Optional<ProductImage> imageOpt = productImageRepository.findById(imageId);
        if (imageOpt.isPresent()) {
            ProductImage image = imageOpt.get();
            
            // If deleting main image, set another image as main
            if (Boolean.TRUE.equals(image.getIsMain())) {
                List<ProductImage> otherImages = productImageRepository.findByProductAndIdNot(image.getProduct(), imageId);
                if (!otherImages.isEmpty()) {
                    ProductImage newMain = otherImages.get(0);
                    newMain.setIsMain(true);
                    productImageRepository.save(newMain);
                }
            }
            
            // Delete file
            try {
                Files.deleteIfExists(Paths.get(image.getImagePath()));
            } catch (IOException e) {
                // Log error but continue with database deletion
                System.err.println("Failed to delete file: " + e.getMessage());
            }
            
            // Delete database record
            productImageRepository.delete(image);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean setMainProductImage(Integer imageId) {
        Optional<ProductImage> imageOpt = productImageRepository.findById(imageId);
        if (imageOpt.isPresent()) {
            ProductImage newMainImage = imageOpt.get();
            
            // Reset current main image
            ProductImage currentMain = productImageRepository.findByProductAndIsMainTrue(newMainImage.getProduct());
            if (currentMain != null) {
                currentMain.setIsMain(false);
                productImageRepository.save(currentMain);
            }
            
            // Set new main image
            newMainImage.setIsMain(true);
            productImageRepository.save(newMainImage);
            return true;
        }
        return false;
    }
    
    @Override
    public ProductImage getProductImage(Integer imageId) {
        return productImageRepository.findById(imageId).orElse(null);
    }
}