package org.uber.service;

import org.uber.dto.Location;
import org.uber.entity.Cab;
import org.uber.enums.CabType;

import java.util.Optional;

public interface CabService {

    void registerCab(Cab cab);

    Optional<Cab> getCab(Location startLocation, CabType cabType);
}
