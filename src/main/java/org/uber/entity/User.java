package org.uber.entity;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Builder
public class User {

    private String userId;
    private String firstName;
    private String lastName;
    // it can be an object with a verification parameter
    private String phoneNumber;
    private String emailId;
    private Long createdAt;
    private Long updatedAt;


}
