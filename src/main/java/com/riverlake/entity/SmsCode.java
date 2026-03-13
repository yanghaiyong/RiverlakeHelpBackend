package com.riverlake.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sms_codes")
public class SmsCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 11)
    private String phone;
    
    @Column(nullable = false, length = 6)
    private String code;
    
    @Column(nullable = false)
    private LocalDateTime expireTime;
    
    @Column(nullable = false)
    private Boolean used = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
