package com.lidexin.business.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 婴幼儿健康档案表
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@TableName("baby_health_record")
public class BabyHealthRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 宝宝ID
     */
    private Long babyId;

    /**
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 身高（cm）
     */
    private BigDecimal height;

    /**
     * 体重（kg）
     */
    private BigDecimal weight;

    /**
     * 头围（cm）
     */
    private BigDecimal headCircumference;

    /**
     * 体温（℃）
     */
    private BigDecimal temperature;

    /**
     * 健康状况
     */
    private String healthStatus;

    /**
     * 疫苗接种情况
     */
    private String vaccination;

    /**
     * 疾病记录
     */
    private String illnessRecord;

    /**
     * 记录人类型（1-家长，2-保姆）
     */
    private Byte recorderType;

    /**
     * 记录人ID
     */
    private Long recorderId;

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

    public Long getBabyId() {
        return babyId;
    }

    public void setBabyId(Long babyId) {
        this.babyId = babyId;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getHeadCircumference() {
        return headCircumference;
    }

    public void setHeadCircumference(BigDecimal headCircumference) {
        this.headCircumference = headCircumference;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getVaccination() {
        return vaccination;
    }

    public void setVaccination(String vaccination) {
        this.vaccination = vaccination;
    }

    public String getIllnessRecord() {
        return illnessRecord;
    }

    public void setIllnessRecord(String illnessRecord) {
        this.illnessRecord = illnessRecord;
    }

    public Byte getRecorderType() {
        return recorderType;
    }

    public void setRecorderType(Byte recorderType) {
        this.recorderType = recorderType;
    }

    public Long getRecorderId() {
        return recorderId;
    }

    public void setRecorderId(Long recorderId) {
        this.recorderId = recorderId;
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
        return "BabyHealthRecord{" +
            "id = " + id +
            ", babyId = " + babyId +
            ", recordDate = " + recordDate +
            ", height = " + height +
            ", weight = " + weight +
            ", headCircumference = " + headCircumference +
            ", temperature = " + temperature +
            ", healthStatus = " + healthStatus +
            ", vaccination = " + vaccination +
            ", illnessRecord = " + illnessRecord +
            ", recorderType = " + recorderType +
            ", recorderId = " + recorderId +
            ", createTime = " + createTime +
            ", updateTime = " + updateTime +
            ", deleted = " + deleted +
        "}";
    }
}
