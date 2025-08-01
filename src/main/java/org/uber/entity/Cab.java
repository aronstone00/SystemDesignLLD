package org.uber.entity;

import lombok.*;
import org.uber.dto.Location;
import org.uber.enums.CabStatus;
import org.uber.enums.CabType;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Builder
public class Cab {
    private String cabId;
    private CabType type;
    private String driverName;
    private Location currentLocation;
    private String registrationNumber;
    private CabStatus status; // IDEAL,ACTIVE, IN_REPAIR,DISABLED


    private Long createdAt;
    private Long updatedAt;
}

