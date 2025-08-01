package org.uber.service;

import org.uber.dto.Location;
import org.uber.entity.Cab;
import org.uber.entity.Journey;
import org.uber.enums.CabType;

import java.math.BigDecimal;
import java.util.Optional;

public interface PriceService {

    BigDecimal getEstimatedPrice(Location startingPoint, Location endingPoint, CabType cabType);

    BigDecimal getFinalPrice(Journey journey, CabType cabType);
}
