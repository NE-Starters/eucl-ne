package com.eucl.rw.serviceImpls;

import com.eucl.rw.model.Meter;
import com.eucl.rw.model.User;
import com.eucl.rw.repository.MeterRepository;
import com.eucl.rw.repository.UserRepository;
import com.eucl.rw.service.MeterService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class MeterServiceImpl implements MeterService {
    private final MeterRepository meterRepository;
    private final UserRepository userRepository;



    @Override
    public Meter registerMeter(UUID userId) {
        String meterNumber = generateMeterNumber();

        if (meterNumber == null || meterNumber.length() != 6) {
            throw new RuntimeException("Meter number must be 6 digits long");
        }
        if (meterRepository.existsByMeterNumber(meterNumber)) {
            throw new RuntimeException("Meter number already exists");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Meter meter = new Meter(meterNumber, user, null);
        return meterRepository.save(meter);
    }

    private String generateMeterNumber() {
        String meterNumber;
        do {
            // Generates 6-digit numbers (000000-999999)
            int randomDigits = (int)(Math.random() * 1_000_000);
            meterNumber = String.format("%06d", randomDigits);
        } while (meterRepository.existsByMeterNumber(meterNumber));
        return meterNumber;
    }

    @Override
    public Meter getMeterByNumber(String meterNumber) {
        return meterRepository.findByMeterNumber(meterNumber)
                .orElseThrow(() -> new RuntimeException("Meter not found"));
    }

    @Override
    public List<Meter> getAllMeters() {
        return meterRepository.findAll();
    }

    @Override
    public List<Meter> getMetersByUser(UUID userId) {
        return meterRepository.findByUser_Id(userId);
    }

    @Override
    public void validateMeterNumber(String meterNumber) {
        if (meterNumber == null || meterNumber.length() != 6) {
            throw new RuntimeException("Invalid meter number format");
        }
    }

    @Override
    public boolean meterExists(String meterNumber) {
        return meterRepository.existsByMeterNumber(meterNumber);
    }

    @Override
    public void deleteMeter(String meterNumber) {
        Meter meter = getMeterByNumber(meterNumber);
        meterRepository.delete(meter);
    }

    @Override
    public void transferMeter(String meterNumber, UUID newUserId) {
        Meter meter = getMeterByNumber(meterNumber);
        User newUser = userRepository.findById(newUserId)
                .orElseThrow(() -> new RuntimeException("New user not found"));
        meter.setUser(newUser);
        meterRepository.save(meter);
    }
}
