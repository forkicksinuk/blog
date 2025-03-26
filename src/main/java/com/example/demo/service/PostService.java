package com.example.demo.service;

import com.example.demo.model.Post;
import com.example.demo.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    public Post createPost(Post post) {
        try {
            return postRepository.save(post);
        } catch (Exception e) {
            logger.error("Error creating post", e);
            return null;
        }
    }

    public Page<Post> findByAuthor(String author, int page) {
        int size = 10; // 每页显示 10 篇文章
        try {
            Pageable pageable = PageRequest.of(page, size);
            return postRepository.findByAuthor(author, pageable);
        } catch (Exception e) {
            logger.error("Error finding posts by author", e);
            return null;
        }
    }

    public Optional<Post> getPostById(Long id){
        return postRepository.findById(id);
    }

    public void deletePost(Long id){
        postRepository.deleteById(id);
    }
    
    public List<Post> getAllPostsOrderByCreateTimeDesc() {
        try {
            return postRepository.findAllByOrderByCreateTimeDesc();
        } catch (Exception e) {
            logger.error("Error finding all posts ordered by create time", e);
            return null;
        }
    }
}
