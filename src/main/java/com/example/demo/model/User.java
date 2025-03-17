package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * User 实体类，映射到数据库中的 `users` 表
 */
@Entity                    // 声明这是一个实体类
@Table(name = "users")     // 指定对应数据库中的表名称为 users
public class User {

    /**
     * 主键字段，使用数据库自动生成（自增）的策略
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名字段，不能为空且唯一
     * @Column 注解中的 nullable 表示字段是否允许为 null
     * unique 表示该字段值在数据库中必须唯一
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 密码字段，不能为空。实际应用中，密码一般需要加密后存储。
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 邮箱字段，可以为空，但如果填写则最好唯一
     */
    @Column(unique = true, length = 100)
    private String email;

    /**
     * 创建时间字段，记录用户注册时间
     * 使用 LocalDateTime 类型，在 JPA 中会自动映射到数据库的 datetime 类型
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    // 构造器：必须提供无参构造器，否则 JPA 在查询时可能会报错
    public User() {
        // 初始化创建时间，当然你也可以在 Service 层处理这个逻辑
        this.createTime = LocalDateTime.now();
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
