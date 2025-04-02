package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "posts")
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "标题不能为空")
    @Size(min = 2, max = 100, message = "标题长度必须在2-100字符之间")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "内容不能为空")
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "author")
    private String author;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Column(name = "dislike_count", columnDefinition = "integer default 0") // Map to dislike_count column with default
    private Integer dislikeCount = 0;

    @Column(name = "like_count", columnDefinition = "integer default 0") // Map to like_count column with default
    private Integer likeCount = 0;

    // Getters and Setters

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    public Integer getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(Integer dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @PrePersist
    public void prePersist(){
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        updateTime = LocalDateTime.now();
    }   
}
