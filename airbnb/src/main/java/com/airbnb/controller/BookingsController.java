package com.airbnb.controller;

import com.airbnb.dto.BookingDto;
import com.airbnb.dto.CustomMultipartFile;
import com.airbnb.entity.Bookings;
import com.airbnb.entity.Property;
import com.airbnb.entity.PropertyUser;
import com.airbnb.repository.BookingsRepository;
import com.airbnb.repository.PropertyRepository;
import com.airbnb.service.BucketService;
import com.airbnb.service.PdfGenerationService;
import com.airbnb.service.SmsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingsController {
    private final SmsService smsService;
    private final BucketService bucketService;
    private final PdfGenerationService pdfGenerationService;
    private final BookingsRepository bookingsRepository;
    private final PropertyRepository propertyRepository;

    public BookingsController(SmsService smsService, BucketService bucketService, PdfGenerationService pdfGenerationService, BookingsRepository bookingsRepository, PropertyRepository propertyRepository) {
        this.smsService = smsService;
        this.bucketService = bucketService;
        this.pdfGenerationService = pdfGenerationService;
        this.bookingsRepository = bookingsRepository;
        this.propertyRepository = propertyRepository;
    }

    @PostMapping("/createBooking/{propertyId}")
    public ResponseEntity<Bookings> createBooking(@RequestBody Bookings bookings,
                                                  @AuthenticationPrincipal PropertyUser user,
                                                  @PathVariable long propertyId) {
        bookings.setPropertyUser(user);
        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        int propertyPrice = property.getNightlyPrice();
        int totalNights = bookings.getTotalNights();
        int totalPrice = propertyPrice * totalNights;
        bookings.setProperty(property);
        bookings.setTotalPrice(totalPrice);
        Bookings createBooking = bookingsRepository.save(bookings);

        BookingDto dto = new BookingDto();
        dto.setBookingId(createBooking.getId());
        dto.setPrice(propertyPrice);
        dto.setGuestName(createBooking.getGuestName());
        dto.setTotalPrice(createBooking.getTotalPrice());

        String pdfFileName = "booking-confirmation-id" + createBooking.getId() + ".pdf";
        Path pdfDirectory = Paths.get("F://feb//");
        Path pdfPath = pdfDirectory.resolve(pdfFileName);

        try {
            // Ensure the directory exists
            if (Files.notExists(pdfDirectory)) {
                Files.createDirectories(pdfDirectory);
            }

            boolean isPdfGenerated = pdfGenerationService.generateBookingPdf(pdfPath.toString(), dto);

            if (isPdfGenerated) {
                File file = pdfPath.toFile();
                MultipartFile multipartFile = new CustomMultipartFile(file);
                String s3Url = bucketService.uploadFile(multipartFile, "myairbnb098");
                smsService.sendSms("+917008430431","Booking confirmed please click here to download details"+ s3Url);
                return new ResponseEntity<>(createBooking, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
