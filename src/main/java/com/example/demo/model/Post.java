package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "posts")
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition =  "TEXT")
    private String content;

    @Column(name = "author")
    private String author;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

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
