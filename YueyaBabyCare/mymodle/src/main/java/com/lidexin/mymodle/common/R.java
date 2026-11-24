package com.lidexin.mymodle.common;

import java.io.Serializable;

/**
 * 商用级统一响应结果封装类
 * 结合枚举类实现标准化的响应格式
 * 
 * @param <T> 响应数据类型
 */
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    public R() {
        this.timestamp = System.currentTimeMillis();
    }

    public R(ResultCode resultCode) {
        this.code = resultCode.code();
        this.message = resultCode.message();
        this.timestamp = System.currentTimeMillis();
    }

    public R(ResultCode resultCode, T data) {
        this.code = resultCode.code();
        this.message = resultCode.message();
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public R(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public R(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // 链式调用方法
    public R<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public R<T> message(String message) {
        this.message = message;
        return this;
    }

    public R<T> data(T data) {
        this.data = data;
        return this;
    }

    // getter和setter方法
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    // 静态方法 - 成功响应
    public static <T> R<T> success() {
        return new R<>(ResultCode.SUCCESS);
    }

    public static <T> R<T> success(T data) {
        return new R<>(ResultCode.SUCCESS, data);
    }

    public static <T> R<T> success(String message, T data) {
        return new R<>(ResultCode.SUCCESS.code(), message, data);
    }

    // 静态方法 - 失败响应
    public static <T> R<T> fail() {
        return new R<>(ResultCode.FAIL);
    }

    public static <T> R<T> fail(String message) {
        return new R<>(ResultCode.FAIL.code(), message);
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        return new R<>(resultCode);
    }

    public static <T> R<T> fail(Integer code, String message) {
        return new R<>(code, message);
    }

    // 静态方法 - 自定义响应
    public static <T> R<T> result(ResultCode resultCode) {
        return new R<>(resultCode);
    }

    public static <T> R<T> result(ResultCode resultCode, T data) {
        return new R<>(resultCode, data);
    }

    // 判断是否成功
    public boolean isSuccess() {
        return ResultCode.SUCCESS.code().equals(this.code);
    }

    @Override
    public String toString() {
        return "R{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}