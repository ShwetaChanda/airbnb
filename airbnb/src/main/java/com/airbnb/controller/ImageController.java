package com.airbnb.controller;

import com.airbnb.entity.Image;
import com.airbnb.entity.Property;
import com.airbnb.entity.PropertyUser;
import com.airbnb.repository.ImageRepository;
import com.airbnb.repository.PropertyRepository;
import com.airbnb.service.BucketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {
    private ImageRepository imageRepository;
    private PropertyRepository propertyRepository;
    private BucketService service;

    public ImageController(ImageRepository imageRepository, PropertyRepository propertyRepository, BucketService service) {
        this.imageRepository = imageRepository;
        this.propertyRepository = propertyRepository;
        this.service = service;
    }
    @PostMapping(path = "/upload/file/{bucketName}/property/{propertyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam MultipartFile file,
                                             @PathVariable String bucketName,
                                             @PathVariable long propertyId ,
                                             @AuthenticationPrincipal PropertyUser propertyUser){
        String imageURL = service.uploadFile(file, bucketName);
        Property property= propertyRepository.findById(propertyId).get();
        Image img= new Image();
        img.setImageURL(imageURL);
        img.setProperty(property);
        img.setPropertyUser(propertyUser);
        Image save = imageRepository.save(img);
        return new ResponseEntity<>(save, HttpStatus.OK);

    }
}
