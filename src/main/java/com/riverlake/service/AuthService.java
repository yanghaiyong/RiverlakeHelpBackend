package com.riverlake.service;

import com.riverlake.dto.LoginResponse;
import com.riverlake.entity.SmsCode;
import com.riverlake.entity.User;
import com.riverlake.repository.SmsCodeRepository;
import com.riverlake.repository.UserRepository;
import com.riverlake.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final SmsCodeRepository smsCodeRepository;
    private final JwtUtil jwtUtil;
    
    private static final int CODE_EXPIRE_MINUTES = 5;
    
    public void sendCode(String phone) {
        User user = userRepository.findByPhone(phone).orElse(null);
        
        if (user != null && user.getStatus() == User.STATUS_PENDING) {
            throw new RuntimeException("账号待审核，请耐心等待");
        }
        
        if (user != null && !user.getEnabled()) {
            throw new RuntimeException("账号已被禁用");
        }
        
        if (user != null && user.getStatus() == User.STATUS_UNREGISTERED) {
            throw new RuntimeException("用户未注册，请先注册");
        }
        
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(generateCode());
        smsCode.setExpireTime(LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES));
        smsCode.setUsed(false);
        
        smsCodeRepository.save(smsCode);
        
        System.out.println("【RiverLake Help】验证码: " + smsCode.getCode() + "，有效期" + CODE_EXPIRE_MINUTES + "分钟");
    }
    
    @Transactional
    public LoginResponse login(String phone, String code) {
        SmsCode smsCode = smsCodeRepository
                .findTopByPhoneAndUsedFalseOrderByCreatedAtDesc(phone)
                .orElseThrow(() -> new RuntimeException("验证码不存在或已使用"));
        
        if (smsCode.isExpired()) {
            throw new RuntimeException("验证码已过期");
        }
        
        if (!smsCode.getCode().equals(code)) {
            throw new RuntimeException("验证码错误");
        }
        
        smsCode.setUsed(true);
        smsCodeRepository.save(smsCode);
        
        User user = userRepository.findByPhone(phone).orElse(null);
        
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setName("用户" + phone.substring(7));
            user.setStatus(User.STATUS_REGISTERED);
            user.setEnabled(true);
            user = userRepository.save(user);
        } else if (user.getStatus() == User.STATUS_PENDING) {
            throw new RuntimeException("账号待审核，请耐心等待");
        } else if (user.getStatus() == User.STATUS_UNREGISTERED) {
            user.setStatus(User.STATUS_REGISTERED);
            user = userRepository.save(user);
        }
        
        if (!user.getEnabled()) {
            throw new RuntimeException("账号已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());
        
        return new LoginResponse(
                token,
                new LoginResponse.UserInfo(user.getId(), user.getPhone(), user.getName(), user.getAvatar())
        );
    }
    
    private String generateCode() {
        // 测试环境使用固定验证码
        return "888888";
        // return String.format("%06d", new Random().nextInt(1000000));
    }
}
