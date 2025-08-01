package org.uber.dto;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Builder
public class BookCabResponse {
    private String OTP;
    private String driverId;
    private String driverName;
}
