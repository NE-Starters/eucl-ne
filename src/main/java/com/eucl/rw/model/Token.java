package com.eucl.rw.model;

import com.eucl.rw.enums.ETokenStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "purchased_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JsonBackReference("meter-token")
    @JoinColumn(name = "meter_number", referencedColumnName = "meter_number")
    private Meter meter;

    // Optional – only if needed
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-token")
    private User user;

    @Column(length = 16, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_status")
    private ETokenStatus tokenStatus;

    @Column(name = "token_value_days")
    private Integer tokenValueDays;

    @Column(name = "purchased_date")
    private LocalDateTime purchasedDate;

    private Double amount;

    public String getFormattedToken() {
        return token.replaceAll("(.{4})(?=.)", "$1-");
    }
}
