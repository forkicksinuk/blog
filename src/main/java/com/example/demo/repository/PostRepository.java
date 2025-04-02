package com.example.demo.repository;

import com.example.demo.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    Page<Post> findByAuthor(String author, Pageable pageable);
    
    // 按创建时间降序排列所有帖子
    Page<Post> findAllByOrderByCreateTimeDesc(Pageable pageable);
    
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :id")
    void incrementLikeCount(Long id);
    
    @Modifying
    @Query("UPDATE Post p SET p.dislikeCount = p.dislikeCount + 1 WHERE p.id = :id")
    void incrementDislikeCount(Long id);
    
    @Query("SELECT p.likeCount, p.dislikeCount FROM Post p WHERE p.id = :id")
    Object[] getVoteCounts(Long id);
}
