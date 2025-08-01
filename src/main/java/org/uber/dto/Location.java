package org.uber.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Builder
public class Location {
    private String latitude;
    private String longitude;
}
