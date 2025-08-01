package org.uber.entity;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Builder
public class Payments {
    private String id;
    private String journeyId;
    private BigDecimal amount;
    private String paymentMode;
    private String  status;
    private Long createdAt;
    private Long updatedAt;
}
