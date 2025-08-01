package org.uber.dto;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
@Builder
public class CheckForBookingResponse {
    private boolean isRideAvailable;
    private BigDecimal estimatedCost;

}
