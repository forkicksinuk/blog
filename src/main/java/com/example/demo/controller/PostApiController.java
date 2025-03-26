package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostApiController {

    @Autowired
    private PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id){
        Optional<Post> post = postService.getPostById(id);
        if(post.isPresent()){
            return new ResponseEntity<>(post.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
