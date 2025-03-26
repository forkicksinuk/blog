package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.model.Post;
import com.example.demo.service.PostService;

@Controller
public class ViewController {
    
    @Autowired
    private PostService postService;

    /**
     * 自定义登录页面
     */
    @GetMapping("/auth/login")
    public String login(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
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
     * 主页 - 显示所有贴子列表
     */
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<Post> posts = postService.getAllPostsOrderByCreateTimeDesc();
        model.addAttribute("posts", posts);
        return "home";
    }
    
    /**
     * 新建贴子页面
     */
    @GetMapping("/posts/new")
    public String newPost(Model model) {
        model.addAttribute("post", new Post());
        return "post-form";
    }
    
    /**
     * 贴子详情页面
     */
    @GetMapping("/posts/{id}")
    public String postDetail(@PathVariable Long id, Model model) {
        Optional<Post> postOpt = postService.getPostById(id);
        if (postOpt.isPresent()) {
            model.addAttribute("post", postOpt.get());
            return "post-detail";
        } else {
            return "redirect:/home";
        }
    }
    
    /**
     * 注册页面
     */
    @GetMapping("/auth/register")
    public String register() {
        return "register";
    }
}
