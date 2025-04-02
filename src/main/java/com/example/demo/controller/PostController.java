package com.example.demo.controller;

import com.example.demo.exception.PostCreationException; // Import custom exception
import com.example.demo.exception.ResourceNotFoundException; // Import ResourceNotFoundException
import com.example.demo.model.Post;
import com.example.demo.service.PostService;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.security.Principal;
// import java.util.Optional; // 不再需要 Optional
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/posts")
@Validated
public class PostController {

    // DTO for receiving vote requests
    public static class VoteRequest {
        private String voteType;
        
        public String getVoteType() {
            return voteType;
        }
        
        public void setVoteType(String voteType) {
            this.voteType = voteType;
        }
    }

    // Simple authenticated endpoint to help refresh CSRF token cookie if needed
    @GetMapping("/ping")
    public ResponseEntity<Void> ping() {
        // Just return OK, the main purpose is to go through the security filter
        return ResponseEntity.ok().build();
    }

    @Autowired
    private PostService postService;

    private static final Logger logger = LoggerFactory.getLogger(PostController.class); // Add logger

    @PostMapping("/save")
    public ResponseEntity<?> savePost(@Valid @RequestBody Post post, Principal principal) { // Change return type to ResponseEntity<?>
        if (principal == null) {
            logger.warn("Attempt to create post without authentication.");
            // Return 401 Unauthorized if user is not authenticated
            return new ResponseEntity<>("User not authenticated", HttpStatus.UNAUTHORIZED);
        }

        try {
            post.setAuthor(principal.getName());
            Post savedPost = postService.createPost(post);
            return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
        } catch (PostCreationException e) {
            // Log the specific exception from the service layer
            logger.error("Failed to create post: {}", e.getMessage(), e);
            // Return 500 Internal Server Error with a meaningful message
            return new ResponseEntity<>("Error creating post: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            logger.error("Unexpected error during post creation", e);
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 更新 getPostById 以适应 Service 层的更改
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable @NotNull Long id) {
        // Service 层现在会在找不到时抛出 ResourceNotFoundException
        // GlobalExceptionHandler 会处理这个异常并返回 404
        // 因此这里只需要成功时返回 Post
        Post post = postService.getPostById(id);
        return new ResponseEntity<>(post, HttpStatus.OK);
        // 不再需要手动检查 Optional 和返回 404
    }

    // 更新 deletePost 以适应 Service 层的更改
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable @NotNull Long id) {
        // 首先检查帖子是否存在，如果不存在，getPostById 会抛出 ResourceNotFoundException
        // GlobalExceptionHandler 会处理这个异常并返回 404
        postService.getPostById(id); // 确认帖子存在，否则抛出异常

        // 如果帖子存在，则执行删除
        try {
            postService.deletePost(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            // 记录删除操作中的其他潜在错误
            logger.error("Error deleting post with id: {}", id, e);
            // GlobalExceptionHandler 会处理这里的通用 Exception
            throw e; // 重新抛出，让全局处理器处理
            // 或者返回 return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 处理投票（点赞/点踩）请求
     * 
     * @param id 帖子ID
     * @param voteRequest 包含投票类型的请求体
     * @param principal 当前登录用户的信息
     * @return 更新后的点赞/点踩计数
     */
    @PostMapping("/{id}/vote")
    public ResponseEntity<?> vote(
            @PathVariable @NotNull Long id,
            @RequestBody VoteRequest voteRequest,
            Principal principal) {
        
        // 检查用户是否已登录
        if (principal == null) {
            logger.warn("Attempt to vote without authentication.");
            return new ResponseEntity<>("请先登录再进行投票", HttpStatus.UNAUTHORIZED);
        }
        
        try {
            // 调用服务层方法处理投票请求
            PostService.VoteResult result = postService.handleVote(id, voteRequest.getVoteType());
            
            // 返回更新后的计数
            return ResponseEntity.ok()
                .body(result);
                
        } catch (ResourceNotFoundException e) {
            logger.error("Attempt to vote for non-existent post", e);
            return new ResponseEntity<>("帖子不存在", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid vote type", e);
            return new ResponseEntity<>("无效的投票类型", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error processing vote", e);
            return new ResponseEntity<>("处理投票时发生错误", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 保留现有的上下投票方法，以保持兼容性
    @PostMapping("/{id}/upvote")
    public ResponseEntity<Void> upvote(@PathVariable @NotNull Long id) {
        try {
            postService.upvote(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/downvote")
    public ResponseEntity<Void> downvote(@PathVariable @NotNull Long id) {
        try {
            postService.downvote(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
