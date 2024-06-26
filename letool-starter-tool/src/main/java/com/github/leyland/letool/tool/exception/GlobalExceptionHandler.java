package com.github.leyland.letool.tool.exception;

import cn.hutool.core.util.StrUtil;
import com.github.leyland.letool.tool.api.ResponseEntity;
import com.github.leyland.letool.tool.api.SystemResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.Servlet;
import java.util.Set;

/**
 * @ClassName <h2>GlobalExceptionHandler</h2>
 * @Description TODO 全局异常拦截器
 * @Author Rungo
 * @Version 1.0
 **/
@Order(Ordered.LOWEST_PRECEDENCE)
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@ConditionalOnMissingBean(GlobalExceptionHandler.class)
@RestControllerAdvice
@SuppressWarnings("all")
public class GlobalExceptionHandler {

    private Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity handleError(MissingServletRequestParameterException e) {
        log.warn("【缺少请求参数】：{}", e.getParameterName());
        String message = String.format("【缺少必要的请求参数】: %s", e.getParameterName());
        return ResponseEntity.fail(SystemResultCode.PARAM_MISS, message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity handleError(MethodArgumentTypeMismatchException e) {
        log.warn("【请求参数格式错误】：{}", e.getName());
        String message = String.format("【请求参数格式错误】: %s", e.getName());
        return ResponseEntity.fail(SystemResultCode.PARAM_TYPE_ERROR, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity handleError(MethodArgumentNotValidException e) {
        log.warn("【参数验证失败】：{}", e.getBindingResult());
        return handleError(e.getBindingResult());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity handleError(BindException e) {
        log.warn("【参数绑定失败】：{}", e.getBindingResult());
        return handleError(e.getBindingResult());
    }

    @SuppressWarnings("all")
    private ResponseEntity handleError(BindingResult result) {
        FieldError error = result.getFieldError();
        String message = String.format("%s:%s", error.getField(), error.getDefaultMessage());
        return ResponseEntity.fail(SystemResultCode.PARAM_BIND_ERROR, message);
    }

    /*@ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity handleError(ConstraintViolationException e) {
        log.warn("【参数验证失败】：{}", e.getMessage());
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String path = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
        String message = String.format("%s:%s", path, violation.getMessage());
        return ResponseEntity.fail(SystemResultCode.PARAM_VALID_ERROR, message);
    }*/

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity handleError(NoHandlerFoundException e) {
        log.error("【404没找到请求】：{}", e.getMessage());
        return ResponseEntity.fail(SystemResultCode.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity handleError(HttpMessageNotReadableException e) {
        log.error("【消息不能读取】：{}", e.getMessage());
        return ResponseEntity.fail(SystemResultCode.MSG_NOT_READABLE, e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity handleError(HttpRequestMethodNotSupportedException e) {
        log.error("【不支持当前请求方法】：{}", e.getMessage());
        return ResponseEntity.fail(SystemResultCode.METHOD_NOT_SUPPORTED, e.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity handleError(HttpMediaTypeNotSupportedException e) {
        log.error("【不支持当前媒体类型】：{}", e.getMessage());
        return ResponseEntity.fail(SystemResultCode.MEDIA_TYPE_NOT_SUPPORTED, e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity handleError(BusinessException e) {
        log.error("【业务异常】：{}", e.getMessage());
        return ResponseEntity.fail(e.getResultCode(), e.getMessage());
    }

    @ExceptionHandler(LetoolSecurityException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity handleError(LetoolSecurityException e) {
        log.error("【认证异常】：{}", e.getMessage());
        return ResponseEntity.fail(e.getResultCode(), "【认证异常】：" + e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity handleError(Throwable e) {
        log.error("【服务器异常】：{}", e.getMessage());
        e.printStackTrace();
        return ResponseEntity.fail(SystemResultCode.INTERNAL_SERVER_ERROR, (StrUtil.isEmpty(e.getMessage()) ? SystemResultCode.INTERNAL_SERVER_ERROR.getMessage() : e.getMessage()));
    }
}
