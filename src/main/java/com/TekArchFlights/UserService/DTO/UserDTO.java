package com.TekArchFlights.UserService.DTO;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {

    private Long userId;
    private String username;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default Constructor
    public UserDTO() {}

    // Parameterized Constructor
    public UserDTO(Long userId, String username, String email, String phone, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters are automatically generated by @Data from Lombok
}

