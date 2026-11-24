package com.lidexin.mymodle.common;

/**
 * 响应码枚举类
 * 定义系统中所有可能的响应状态码和对应的消息
 */
public enum ResultCode {
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 通用错误
    FAIL(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    
    // 用户相关错误
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_ACCOUNT_LOCKED(1002, "账号已被锁定"),
    USER_PASSWORD_ERROR(1003, "密码错误"),
    USER_ROLE_MISMATCH(1004, "用户角色不匹配"),
    
    // 预约相关错误
    APPOINTMENT_FULL(2001, "号源已满"),
    APPOINTMENT_NOT_EXIST(2002, "预约信息不存在"),
    APPOINTMENT_CANCELLED(2003, "预约已取消"),
    APPOINTMENT_COMPLETED(2004, "预约已完成"),
    
    // 医生排班相关错误
    SCHEDULE_NOT_EXIST(3001, "排班信息不存在"),
    SCHEDULE_STOPPED(3002, "排班已停诊"),
    
    // 系统错误
    SYSTEM_ERROR(9999, "系统内部错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return code;
    }

    public String message() {
        return message;
    }
}