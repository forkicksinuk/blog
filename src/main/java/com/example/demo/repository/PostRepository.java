package com.example.demo.repository;

import com.example.demo.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    Page<Post> findByAuthor(String author, Pageable pageable);
    
    // 按创建时间降序排列所有帖子
    List<Post> findAllByOrderByCreateTimeDesc();
}
