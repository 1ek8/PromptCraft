package com.promptcraft.promptcraft.repository;

import com.promptcraft.promptcraft.entity.UsageLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UsageLogRepository extends JpaRepository<UsageLog, Long> {

//    Object findByUserIdAndDate(Long userId, LocalDate today);
    Optional<UsageLog> findByUserIdAndDate(Long userId, LocalDate today);
}
