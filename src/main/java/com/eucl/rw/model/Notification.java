package com.eucl.rw.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JsonBackReference("user-notification")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "meter_number", length = 6)
    private String meterNumber;

    private String message;

    @Column(name = "issued_date")
    private LocalDateTime issuedDate;

    private boolean emailed;
}