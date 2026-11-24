package com.lidexin.business.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>
 * 保姆排班表
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@TableName("nanny_schedule")
public class NannySchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 排班日期
     */
    private LocalDate scheduleDate;

    /**
     * 班次类型（1-早班，2-中班，3-晚班，4-全天）
     */
    private Byte shiftType;

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
     * 调班目标保姆ID
     */
    private Long swapTargetNannyId;
    
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
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标志（0-未删除，1-已删除）
     */
    private Byte deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public Long getNannyId() {
        return nannyId;
    }

    public void setNannyId(Long nannyId) {
        this.nannyId = nannyId;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Byte getShiftType() {
        return shiftType;
    }

    public void setShiftType(Byte shiftType) {
        this.shiftType = shiftType;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Long getSwapTargetNannyId() {
        return swapTargetNannyId;
    }
    
    public void setSwapTargetNannyId(Long swapTargetNannyId) {
        this.swapTargetNannyId = swapTargetNannyId;
    }
    
    public String getSwapReason() {
        return swapReason;
    }
    
    public void setSwapReason(String swapReason) {
        this.swapReason = swapReason;
    }
    
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Byte getDeleted() {
        return deleted;
    }

    public void setDeleted(Byte deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "NannySchedule{" +
            "id = " + id +
            ", institutionId = " + institutionId +
            ", nannyId = " + nannyId +
            ", scheduleDate = " + scheduleDate +
            ", shiftType = " + shiftType +
            ", startTime = " + startTime +
            ", endTime = " + endTime +
            ", status = " + status +
            ", swapTargetNannyId = " + swapTargetNannyId +
            ", swapReason = " + swapReason +
            ", remark = " + remark +
            ", createTime = " + createTime +
            ", updateTime = " + updateTime +
            ", deleted = " + deleted +
        "}";
    }
}
