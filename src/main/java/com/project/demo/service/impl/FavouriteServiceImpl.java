package com.project.demo.service.impl;

import com.project.demo.model.entity.Favourite;
import com.project.demo.model.entity.Product;
import com.project.demo.model.entity.User;
import com.project.demo.model.repository.FavouriteRepository;
import com.project.demo.service.FavouriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavouriteServiceImpl implements FavouriteService {

    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String INACTIVE_STATUS = "INACTIVE";

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Override
    public List<Favourite> getFavouritesByUser(User user) {
        return favouriteRepository.findByUserAndStatus(user, ACTIVE_STATUS);
    }

    @Override
    public Favourite addToFavourites(User user, Product product) {
        // Check if the product is already in favorites
        Optional<Favourite> existingFavourite = findByUserAndProduct(user, product);

        if (existingFavourite.isPresent()) {
            // If it exists but is inactive, reactivate it
            Favourite favourite = existingFavourite.get();
            if (!ACTIVE_STATUS.equals(favourite.getStatus())) {
                favourite.setStatus(ACTIVE_STATUS);
                return favouriteRepository.save(favourite);
            }
            // If it's already active, just return it
            return favourite;
        } else {
            // Create a new favorite
            Favourite favourite = new Favourite();
            favourite.setUser(user);
            favourite.setProduct(product);
            favourite.setStatus(ACTIVE_STATUS);
            return favouriteRepository.save(favourite);
        }
    }

    @Override
    public void removeFromFavourites(User user, Product product) {
        Optional<Favourite> existingFavourite = findByUserAndProduct(user, product);

        existingFavourite.ifPresent(favourite -> {
            // Set status to inactive instead of deleting
            favourite.setStatus(INACTIVE_STATUS);
            favouriteRepository.save(favourite);
        });
    }

    @Override
    public boolean isProductInFavourites(User user, Product product) {
        Optional<Favourite> favourite = findByUserAndProduct(user, product);
        return favourite.isPresent() && ACTIVE_STATUS.equals(favourite.get().getStatus());
    }

    @Override
    public Optional<Favourite> findByUserAndProduct(User user, Product product) {
        return favouriteRepository.findByUserAndProduct(user, product);
    }
}
