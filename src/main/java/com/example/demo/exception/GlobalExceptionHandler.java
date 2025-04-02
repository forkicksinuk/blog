package com.example.demo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError; // 导入 FieldError
import org.springframework.web.bind.MethodArgumentNotValidException; // 导入 MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap; // 导入 HashMap
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 通用异常处理
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        String errorMessage = "发生意外错误，请联系管理员。";
        logger.error("未捕获的异常: {}", ex.getMessage(), ex); // 记录详细错误信息和堆栈跟踪

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        body.put("message", errorMessage);
        body.put("path", request.getDescription(false).replace("uri=", "")); // 获取请求路径
        // 可以在这里添加更多上下文信息，例如异常类型
        body.put("exceptionType", ex.getClass().getName());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 在这里可以添加更多针对特定异常的处理方法
    // 例如：处理 PostCreationException
    @ExceptionHandler(PostCreationException.class)
    public ResponseEntity<Object> handlePostCreationException(PostCreationException ex, WebRequest request) {
        logger.warn("帖子创建失败: {}", ex.getMessage()); // 使用 warn 级别记录业务逻辑错误

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value()); // 或者根据具体情况选择状态码
        body.put("error", "Post Creation Failed");
        body.put("message", ex.getMessage()); // 使用异常中定义的具体消息
        body.put("path", request.getDescription(false).replace("uri=", ""));
        body.put("exceptionType", ex.getClass().getName());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 可以继续添加处理其他自定义异常或 Spring 内置异常的方法
    // 例如：处理资源未找到的情况
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.warn("资源未找到: {}", ex.getMessage()); // 记录警告信息

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        body.put("message", ex.getMessage()); // 使用异常中定义的具体消息
        body.put("path", request.getDescription(false).replace("uri=", ""));
        body.put("exceptionType", ex.getClass().getName());
        // 可以选择性地添加更多细节
        // body.put("resourceName", ex.getResourceName());
        // body.put("fieldName", ex.getFieldName());
        // body.put("fieldValue", ex.getFieldValue());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // 处理数据校验异常 (例如使用 @Valid 注解时)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.warn("数据校验失败: {}", errors); // 记录具体的校验错误

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("message", "请求参数校验失败"); // 通用校验失败消息
        body.put("details", errors); // 包含具体的字段错误信息
        body.put("path", request.getDescription(false).replace("uri=", ""));
        body.put("exceptionType", ex.getClass().getName());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
