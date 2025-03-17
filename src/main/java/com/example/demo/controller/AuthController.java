package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证和用户管理控制器
 * 处理用户注册、登录相关功能
 */
@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 表单提交方式注册
     */
    @PostMapping(value = "/perform-register", consumes = "application/x-www-form-urlencoded")
    public String registerForm(User user, RedirectAttributes redirectAttributes) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()) != null) {
            redirectAttributes.addFlashAttribute("error", "用户名已存在");
            return "redirect:/login";
        }
        
        try {
            // 加密密码并保存用户
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            
            redirectAttributes.addFlashAttribute("success", "注册成功，请登录");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "注册失败: " + e.getMessage());
            return "redirect:/register";
        }
    }
}
