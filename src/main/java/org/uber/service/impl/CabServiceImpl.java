package org.uber.service.impl;

import org.uber.dto.Location;
import org.uber.entity.Cab;
import org.uber.enums.CabStatus;
import org.uber.enums.CabType;
import org.uber.service.CabService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class CabServiceImpl implements CabService {


    private final Map<CabType, HashSet<Cab>> cabType;
    private final Map<String, Cab> cabregistry;

    public CabServiceImpl() {
        this.cabregistry = new ConcurrentHashMap<>();
        cabType = new HashMap<>();
        Arrays.stream(CabType.values()).forEach(cabType -> {
            this.cabType.put(cabType, new HashSet<>());
        });
    }

    @Override
    public void registerCab(Cab cab) {
        HashSet<Cab> currentList = cabType.getOrDefault(cab.getType(), new HashSet<>());
        currentList.add(cab);
        cabType.put(cab.getType(), currentList);
        cabregistry.put(cab.getCabId(), cab);
    }

    @Override
    public Optional<Cab> getCab(Location startLocation, CabType cabType) {
        List<Cab> cabs = this.cabType.get(cabType).stream().filter(cab -> cab.getStatus().equals(CabStatus.IDEAL))
                .filter(cab -> checkIfLocationNear(cab, startLocation)).toList();
        return cabs.isEmpty() ? Optional.empty() : Optional.of(cabs.get(0));
    }

    private boolean checkIfLocationNear(Cab cab, Location startLocation) {
        //logic of lat and long;
        return true;
    }

}
