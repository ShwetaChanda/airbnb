package com.airbnb.controller;

import com.airbnb.entity.Favourite;
import com.airbnb.entity.PropertyUser;
import com.airbnb.repository.FavouriteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/favourite")
public class FavouriteController {
     private FavouriteRepository favouriteRepository;

    public FavouriteController(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }

    @PostMapping
    public ResponseEntity<Favourite> addFavourite(@AuthenticationPrincipal PropertyUser user, @RequestBody Favourite favourite){
        favourite.setPropertyUser(user);
        Favourite save = favouriteRepository.save(favourite);
        System.out.println(save);
        return new ResponseEntity<>(save, HttpStatus.CREATED);


    }
}
