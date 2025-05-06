package com.eucl.rw.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "meters")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Meter {
    @Id
    @Column(name = "meter_number", length = 6)
    private String meterNumber;

    @ManyToOne
    @JsonBackReference("user-meter")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "meter", cascade = CascadeType.ALL)
    @JsonManagedReference("meter-token")
    private Set<Token> tokens;

}