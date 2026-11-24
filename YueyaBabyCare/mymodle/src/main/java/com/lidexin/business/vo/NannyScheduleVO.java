package com.lidexin.business.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 保姆排班VO
 *
 * @author 张三
 * @since 2025-12-25
 */
@Data
public class NannyScheduleVO {
    
    /**
     * 排班ID
     */
    private Long id;
    
    /**
     * 机构ID
     */
    private Long institutionId;
    
    /**
     * 保姆ID
     */
    private Long nannyId;
    
    /**
     * 保姆姓名
     */
    private String nannyName;
    
    /**
     * 保姆手机号
     */
    private String nannyPhone;
    
    /**
     * 排班日期
     */
    private LocalDate scheduleDate;
    
    /**
     * 班次类型（1-早班，2-中班，3-晚班，4-全天）
     */
    private Byte shiftType;
    
    /**
     * 班次名称
     */
    private String shiftTypeName;
    
    /**
     * 上班时间
     */
    private LocalTime startTime;
    
    /**
     * 下班时间
     */
    private LocalTime endTime;
    
    /**
     * 状态（0-取消，1-正常，2-调班申请中，3-调班已批准）
     */
    private Byte status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 调班目标保姆ID
     */
    private Long swapTargetNannyId;
    
    /**
     * 调班目标保姆姓名
     */
    private String swapTargetNannyName;
    
    /**
     * 调班原因
     */
    private String swapReason;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 获取班次名称
     */
    public static String getShiftTypeName(Byte shiftType) {
        if (shiftType == null) return "未知";
        switch (shiftType) {
            case 1: return "早班";
            case 2: return "中班";
            case 3: return "晚班";
            case 4: return "全天";
            default: return "未知";
        }
    }
    
    /**
     * 获取状态名称
     */
    public static String getStatusName(Byte status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "已取消";
            case 1: return "正常";
            case 2: return "调班申请中";
            case 3: return "调班已批准";
            default: return "未知";
        }
    }
}
