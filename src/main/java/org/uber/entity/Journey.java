package org.uber.entity;

import lombok.*;
import org.uber.dto.Location;
import org.uber.enums.JourneyStatus;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Builder
public class Journey {
    private String id;
    private String userId;
    private String driverId;
    private String paymentId;
    private Long bookingTime;
    private JourneyStatus status;
    private Location startingPoint;
    private Location endingPoint;
    private Long createdAt;
    private Long updatedAt;
}
