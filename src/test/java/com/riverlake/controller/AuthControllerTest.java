package com.riverlake.controller;

import com.riverlake.entity.SmsCode;
import com.riverlake.entity.User;
import com.riverlake.repository.SmsCodeRepository;
import com.riverlake.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmsCodeRepository smsCodeRepository;

    private static final String TEST_PHONE = "13900001111";
    private static final String TEST_CODE = "888888";

    @BeforeEach
    void setUp() {
        smsCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void createSmsCode(String phone) {
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(TEST_CODE);
        smsCode.setExpireTime(LocalDateTime.now().plusMinutes(5));
        smsCode.setUsed(false);
        smsCodeRepository.save(smsCode);
    }

    @Test
    void sendCode_unregisteredUser_shouldSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + TEST_PHONE + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("验证码已发送"));
    }

    @Test
    void sendCode_pendingUser_shouldReturnPendingMessage() throws Exception {
        User user = new User();
        user.setPhone(TEST_PHONE);
        user.setName("测试用户");
        user.setStatus(User.STATUS_PENDING);
        user.setEnabled(true);
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + TEST_PHONE + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("账号待审核，请耐心等待"));
    }

    @Test
    void sendCode_disabledUser_shouldReturnDisabledMessage() throws Exception {
        User user = new User();
        user.setPhone(TEST_PHONE);
        user.setName("测试用户");
        user.setStatus(User.STATUS_REGISTERED);
        user.setEnabled(false);
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + TEST_PHONE + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("账号已被禁用"));
    }

    @Test
    void sendCode_unregisteredStatusUser_shouldReturnUnregisteredMessage() throws Exception {
        User user = new User();
        user.setPhone(TEST_PHONE);
        user.setName("测试用户");
        user.setStatus(User.STATUS_UNREGISTERED);
        user.setEnabled(true);
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + TEST_PHONE + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("用户未注册，请先注册"));
    }

    @Test
    void sendCode_registeredUser_shouldSuccess() throws Exception {
        User user = new User();
        user.setPhone(TEST_PHONE);
        user.setName("测试用户");
        user.setStatus(User.STATUS_REGISTERED);
        user.setEnabled(true);
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + TEST_PHONE + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("验证码已发送"));
    }

    @Test
    void login_unregisteredUser_shouldAutoRegister() throws Exception {
        mockMvc.perform(post("/api/auth/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + TEST_PHONE + "\"}"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + TEST_PHONE + "\",\"code\":\"" + TEST_CODE + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.phone").value(TEST_PHONE));

        User user = userRepository.findByPhone(TEST_PHONE).orElse(null);
        assertNotNull(user);
        assertEquals(User.STATUS_REGISTERED, user.getStatus());
    }

    @Test
    void login_registeredUser_shouldSuccess() throws Exception {
        User user = new User();
        user.setPhone(TEST_PHONE);
        user.setName("测试用户");
        user.setStatus(User.STATUS_REGISTERED);
        user.setEnabled(true);
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + TEST_PHONE + "\"}"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + TEST_PHONE + "\",\"code\":\"" + TEST_CODE + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.phone").value(TEST_PHONE));
    }

    @Test
    void login_pendingUser_shouldReturnPendingMessage() throws Exception {
        User user = new User();
        user.setPhone(TEST_PHONE);
        user.setName("测试用户");
        user.setStatus(User.STATUS_PENDING);
        user.setEnabled(true);
        userRepository.save(user);

        createSmsCode(TEST_PHONE);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + TEST_PHONE + "\",\"code\":\"" + TEST_CODE + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("账号待审核，请耐心等待"));
    }

    @Test
    void login_disabledUser_shouldReturnDisabledMessage() throws Exception {
        User user = new User();
        user.setPhone(TEST_PHONE);
        user.setName("测试用户");
        user.setStatus(User.STATUS_REGISTERED);
        user.setEnabled(false);
        userRepository.save(user);

        createSmsCode(TEST_PHONE);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + TEST_PHONE + "\",\"code\":\"" + TEST_CODE + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("账号已被禁用"));
    }

    @Test
    void login_unregisteredStatusUser_shouldAutoRegister() throws Exception {
        User user = new User();
        user.setPhone(TEST_PHONE);
        user.setName("测试用户");
        user.setStatus(User.STATUS_UNREGISTERED);
        user.setEnabled(true);
        userRepository.save(user);

        createSmsCode(TEST_PHONE);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + TEST_PHONE + "\",\"code\":\"" + TEST_CODE + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        User updatedUser = userRepository.findByPhone(TEST_PHONE).orElse(null);
        assertNotNull(updatedUser);
        assertEquals(User.STATUS_REGISTERED, updatedUser.getStatus());
    }

    @Test
    void login_invalidCode_shouldReturnError() throws Exception {
        User user = new User();
        user.setPhone(TEST_PHONE);
        user.setName("测试用户");
        user.setStatus(User.STATUS_REGISTERED);
        user.setEnabled(true);
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + TEST_PHONE + "\"}"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + TEST_PHONE + "\",\"code\":\"000000\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("验证码错误"));
    }
}
