package com.eucl.rw.service;

import com.eucl.rw.model.Meter;

import java.util.List;
import java.util.UUID;


public interface MeterService {
    Meter registerMeter(UUID userId);
    Meter getMeterByNumber(String meterNumber);
    List<Meter> getAllMeters();
    List<Meter> getMetersByUser(UUID userId);
    void validateMeterNumber(String meterNumber);
    boolean meterExists(String meterNumber);
    void deleteMeter(String meterNumber);
    void transferMeter(String meterNumber, UUID newUserId);
}
