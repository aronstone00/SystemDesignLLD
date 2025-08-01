package org.uber.service.impl;

import org.uber.dto.BookCabResponse;
import org.uber.dto.CheckForBookingResponse;
import org.uber.dto.Location;

import org.uber.entity.Cab;
import org.uber.entity.Journey;
import org.uber.entity.User;
import org.uber.enums.CabType;
import org.uber.service.BookingService;
import org.uber.service.CabService;
import org.uber.service.JourneyService;
import org.uber.service.PriceService;

import java.util.Optional;

public class BookingServiceImpl implements BookingService {
    private final CabService cabService;
    private final PriceService priceService;
    private final JourneyService journeyService;

    public BookingServiceImpl(CabService cabService, PriceService priceService, JourneyService journeyService) {
        this.cabService = cabService;
        this.priceService = priceService;
        this.journeyService = journeyService;
    }

    @Override
    public CheckForBookingResponse checkForBooking(Location startingPoint, Location endingPoint, CabType cabType) {
        Optional<Cab> cab = cabService.getCab(startingPoint, cabType);
        if (cab.isEmpty()) {
            return CheckForBookingResponse.builder()
                    .estimatedCost(null)
                    .isRideAvailable(false)
                    .build();
        }
        return CheckForBookingResponse.builder()
                .estimatedCost(priceService.getEstimatedPrice(startingPoint, endingPoint, cabType))
                .isRideAvailable(true)
                .build();
    }

    @Override
    public BookCabResponse bookCab(User user, Location startingPoint, Location endingPoint, CabType cabType) throws InterruptedException {
        Optional<Cab> cabOpt = cabService.getCab(startingPoint, cabType);
        if(cabOpt.isEmpty()){
            throw new RuntimeException("cab not available");
        }
        Cab cab = cabOpt.get();
        Journey journey =  journeyService.createjourney(user,cab,startingPoint,endingPoint);
        Thread.sleep(3);
        journeyService.updateJourneyWithDriverPickup(journey);

        return BookCabResponse.builder().build();

    }
}
