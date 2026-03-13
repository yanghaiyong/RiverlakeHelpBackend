package com.riverlake.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    
    public static final int STATUS_UNREGISTERED = 0;
    public static final int STATUS_PENDING = 1;
    public static final int STATUS_REGISTERED = 2;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 11)
    private String phone;
    
    @Column(length = 50)
    private String name;
    
    @Column(length = 255)
    private String avatar;
    
    @Column(nullable = false)
    private Boolean enabled = true;
    
    @Column(nullable = false)
    private Integer status = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
