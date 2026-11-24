package com.lidexin.business.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 机构表
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Data
@TableName("institution")
public class Institution implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（MD5加密）
     */
    private String password;

    /**
     * 机构名称
     */
    private String name;

    /**
     * 机构LOGO
     */
    private String logo;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 营业执照号
     */
    private String licenseNo;

    /**
     * 营业执照图片
     */
    private String licenseImg;

    /**
     * 机构简介
     */
    private String introduction;

    /**
     * 状态（0-禁用，1-正常，2-待审核）
     */
    private Byte status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标志（0-未删除，1-已删除）
     */
    private Byte deleted;

}
