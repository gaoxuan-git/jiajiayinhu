package com.lidexin.business.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 护理评价DTO
 *
 * @author 张三
 */
public class CareEvaluationDTO {

    /**
     * 护理计划ID
     */
    @NotNull(message = "护理计划ID不能为空")
    private Long carePlanId;

    /**
     * 护理记录ID（可选，可以针对某条记录评价）
     */
    private Long careRecordId;

    /**
     * 评分（1-5分）
     */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低1分")
    @Max(value = 5, message = "评分最高5分")
    private Byte score;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 图片（JSON数组字符串）
     */
    private String images;

    public Long getCarePlanId() {
        return carePlanId;
    }

    public void setCarePlanId(Long carePlanId) {
        this.carePlanId = carePlanId;
    }

    public Long getCareRecordId() {
        return careRecordId;
    }

    public void setCareRecordId(Long careRecordId) {
        this.careRecordId = careRecordId;
    }

    public Byte getScore() {
        return score;
    }

    public void setScore(Byte score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
