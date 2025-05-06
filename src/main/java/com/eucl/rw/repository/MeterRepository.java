package com.eucl.rw.repository;

import com.eucl.rw.model.Meter;
import com.eucl.rw.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface MeterRepository extends JpaRepository<Meter, String> {
    List<Meter> findByUser(User user);
    Optional<Meter> findByMeterNumber(String meterNumber);
    boolean existsByMeterNumber(String meterNumber);
    List<Meter> findByUser_Id(UUID userId);
}
