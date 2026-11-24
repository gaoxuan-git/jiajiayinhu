package com.lidexin.business.enums;

/**
 * 用户类型枚举
 *
 * @author 张三
 * @since 2025-12-21
 */
public enum UserTypeEnum {

    PARENT(1, "家长"),
    NANNY(2, "保姆"),
    INSTITUTION(3, "机构"),
    ADMIN(4, "管理员");

    private final Integer code;
    private final String name;

    UserTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据code获取枚举
     */
    public static UserTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据code获取名称
     */
    public static String getNameByCode(Integer code) {
        UserTypeEnum type = getByCode(code);
        return type != null ? type.getName() : "未知";
    }
}
