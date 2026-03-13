package com.riverlake.repository;

import com.riverlake.entity.SmsCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SmsCodeRepository extends JpaRepository<SmsCode, Long> {
    Optional<SmsCode> findTopByPhoneAndUsedFalseOrderByCreatedAtDesc(String phone);
}
