package com.airbnb.controller;

import com.airbnb.entity.Property;
import com.airbnb.entity.PropertyUser;
import com.airbnb.entity.Review;
import com.airbnb.repository.PropertyRepository;
import com.airbnb.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private PropertyRepository propertyRepository;
    private ReviewRepository reviewRepository;

    public ReviewController(PropertyRepository propertyRepository, ReviewRepository reviewRepository) {
        this.propertyRepository = propertyRepository;
        this.reviewRepository = reviewRepository;
    }

    @PostMapping("/addReviews/{propertyId}")
    public ResponseEntity<String>addReview(@PathVariable long propertyId,
                                           @RequestBody Review review,
                                           @AuthenticationPrincipal PropertyUser user) {
        Optional<Property> byId = propertyRepository.findById(propertyId);
        Property property = byId.get();
        Review r = reviewRepository.findReviewByUser(property, user);
        if (r!=null){
            return new ResponseEntity<>("You have already added review for this property", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        review.setProperty(property);
        review.setPropertyUser(user);
        reviewRepository.save(review);
        return new ResponseEntity<> ("review added successfully", HttpStatus.CREATED);


    }
    @GetMapping("/userReviews")
    public ResponseEntity <List<Review>> grtUserReview(@AuthenticationPrincipal PropertyUser user){
        List <Review> review = reviewRepository.findByPropertyUser(user);
        return new ResponseEntity<>(review,HttpStatus.OK);

    }
}
