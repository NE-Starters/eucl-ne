package com.eucl.rw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MeterDTO {
    private String meterNumber;
    private UUID userId;
}

