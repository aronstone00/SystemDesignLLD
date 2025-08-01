package org.uber.service;

import org.uber.dto.CheckForBookingResponse;
import org.uber.dto.Location;
import org.uber.dto.BookCabResponse;
import org.uber.entity.User;
import org.uber.enums.CabType;

public interface BookingService {

    CheckForBookingResponse checkForBooking(Location startingPoint, Location endingPoint, CabType cabType);



    BookCabResponse bookCab(User user, Location startingPoint, Location endingPoint, CabType cabType) throws InterruptedException;
}
