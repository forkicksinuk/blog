package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    /**
     * 自定义登录页面
     */
    @GetMapping("/auth/login")
    public String login() {
        return "login";
    }
    
    /**
     * 重定向/login到/auth/login，忽略continue参数
     */
    @GetMapping("/login")
    public String redirectLogin() {
        // 不传递continue参数，直接重定向到登录页
        return "redirect:/auth/login";
    }
    
    /**
     * 处理continue路径，防止登录后重定向到不存在的路径
     */
    @GetMapping("/continue")
    public String handleContinue() {
        return "redirect:/home";  // 重定向到主页
    }

    /**
     * 主页
     */
    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }
    
    /**
     * 注册页面
     */
    @GetMapping("/auth/register")
    public String register() {
        return "register";
    }
}
