package com.lidexin.business.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 护理计划签到表
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
@Data
@TableName("care_plan_checkin")
public class CarePlanCheckin implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 预约ID
     */
    private Long appointmentId;

    /**
     * 保姆ID
     */
    private Long nannyId;

    /**
     * 签到类型（1-签到，2-签退）
     */
    private Byte checkinType;

    /**
     * 签到时间
     */
    private LocalDateTime checkinTime;

    /**
     * 签到经度
     */
    private BigDecimal checkinLongitude;

    /**
     * 签到纬度
     */
    private BigDecimal checkinLatitude;

    /**
     * 签到地址
     */
    private String checkinAddress;

    /**
     * 距离服务地址的距离（米）
     */
    private Integer distance;

    /**
     * 是否在范围内（0-否，1-是）
     */
    private Byte isInRange;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
