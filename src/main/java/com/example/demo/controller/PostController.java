package com.example.demo.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.service.PostService;
import com.example.demo.model.Post;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // 处理贴子表单提交
    @PostMapping("/save")
    public String savePost(@ModelAttribute("post") Post post, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal != null) {
            post.setAuthor(principal.getName());
        }

        Post savedPost = postService.createPost(post);
        if (savedPost != null) {
            redirectAttributes.addFlashAttribute("successMessage", "贴子发布成功!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "贴子发布失败，请重试!");
        }

        return "redirect:/home";
    }
}
