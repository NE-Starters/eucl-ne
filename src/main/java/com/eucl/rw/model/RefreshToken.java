package com.eucl.rw.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Schema(description = "Refresh token entity")
public class RefreshToken {

    @Id
    @Schema(description = "Unique identifier of the refresh token", example = "123e4567-e89b-12d3-a456-426614174000")
    private String token;

    @Schema(description = "User ID associated with the token", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @Schema(description = "Expiration date of the token", example = "2025-06-04T08:00:00Z")
    private Instant expiryDate;
}