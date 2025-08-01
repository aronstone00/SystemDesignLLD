package org.uber.service;

import org.uber.dto.Location;
import org.uber.entity.Cab;
import org.uber.entity.Journey;
import org.uber.entity.User;
import org.uber.enums.CabType;

import java.math.BigDecimal;

public interface JourneyService {

    Journey createjourney(User user, Cab cab, Location startingLocation, Location endingLocation);
    void  updateJourneyWithDriverPickup(Journey journey);

}
