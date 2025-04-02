package com.example.demo.service;

import com.example.demo.exception.PostCreationException;
import com.example.demo.exception.ResourceNotFoundException; // 导入 ResourceNotFoundException
import com.example.demo.model.Post;
import com.example.demo.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException; // Import DataAccessException
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
// import java.util.Optional; // 不再需要 Optional


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    public Post createPost(Post post) {
        try {
            return postRepository.save(post);
        } catch (DataAccessException e) { // Catch specific DataAccessException
            logger.error("Error creating post: {}", e.getMessage(), e);
            // Wrap and re-throw as custom exception
            throw new PostCreationException("Failed to create post due to data access error", e);
        } catch (Exception e) { // Catch any other unexpected exceptions
            logger.error("Unexpected error creating post", e);
            throw new PostCreationException("An unexpected error occurred while creating the post", e);
        }
    }

    public Page<Post> findByAuthor(String author, int page) {
        int size = 10; // 每页显示 10 篇文章
        try {
            Pageable pageable = PageRequest.of(page, size);
            return postRepository.findByAuthor(author, pageable);
        } catch (Exception e) {
            logger.error("Error finding posts by author", e);
            return Page.empty(); // Return empty Page instead of null
        }
    }

    // 修改返回类型为 Post，并在找不到时抛出异常
    public Post getPostById(Long id) {
        try {
            return postRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        } catch (ResourceNotFoundException e) {
            // 直接重新抛出 ResourceNotFoundException，让 GlobalExceptionHandler 处理
            throw e;
        } catch (DataAccessException e) { // 捕获更具体的数据库访问异常
            logger.error("Data access error getting post by id: {}", id, e);
            // 可以选择包装成自定义异常或通用运行时异常
            throw new RuntimeException("Error accessing data for post with id: " + id, e);
        } catch (Exception e) { // 捕获其他意外异常
            logger.error("Unexpected error getting post by id: {}", id, e);
            throw new RuntimeException("An unexpected error occurred while retrieving post with id: " + id, e);
        }
    }

    public void deletePost(Long id){
        try {
            postRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting post with id: " + id, e);
            throw e;
        }
    }
    
    // 添加 page 和 size 参数
    public Page<Post> getAllPostsOrderByCreateTimeDesc(int page, int size) {
        try {
            // 创建 Pageable 对象
            Pageable pageable = PageRequest.of(page, size);
            // 传递 Pageable 参数给 Repository 方法
            return postRepository.findAllByOrderByCreateTimeDesc(pageable);
        } catch (Exception e) {
            logger.error("Error finding all posts ordered by create time", e);
            return Page.empty(); // Return empty Page instead of null
        }
    }

    @Transactional
    public void upvote(Long postId) {
        try {
            postRepository.incrementLikeCount(postId);
            return "succeed";
        } catch (Exception e) {
            logger.error("Error upvoting post", e);
            throw e;
        }
    }

    @Transactional
    public void downvote(Long postId) {
        try {
            postRepository.incrementDislikeCount(postId);
            return "succeed";
        } catch (Exception e) {
            logger.error("Error downvoting post", e);
            throw e;
        }
    }

    
    /**
     * 用于存储和传输投票计数的内部类
     */
    public static class VoteResult {
        private final Integer likeCount;
        private final Integer dislikeCount;
        
        public VoteResult(Integer likeCount, Integer dislikeCount) {
            this.likeCount = likeCount;
            this.dislikeCount = dislikeCount;
        }
        
        public Integer getLikeCount() {
            return likeCount;
        }
        
        public Integer getDislikeCount() {
            return dislikeCount;
        }
    }
}
