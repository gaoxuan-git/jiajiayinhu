package com.lidexin.business.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 婴幼儿响应VO
 *
 * @author 张三
 * @since 2025-12-23
 */
@ApiModel(description = "婴幼儿信息")
public class BabyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "婴幼儿ID")
    private Long id;

    @ApiModelProperty(value = "家长ID")
    private Long parentId;

    @ApiModelProperty(value = "宝宝姓名")
    private String name;

    @ApiModelProperty(value = "宝宝昵称")
    private String nickname;

    @ApiModelProperty(value = "性别（0-未知，1-男，2-女）")
    private Byte gender;

    @ApiModelProperty(value = "性别名称")
    private String genderName;

    @ApiModelProperty(value = "出生日期")
    private LocalDate birthDate;

    @ApiModelProperty(value = "年龄（月）")
    private Integer ageInMonths;

    @ApiModelProperty(value = "宝宝头像")
    private String avatar;

    @ApiModelProperty(value = "血型")
    private String bloodType;

    @ApiModelProperty(value = "过敏信息")
    private String allergyInfo;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public String getGenderName() {
        return genderName;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getAgeInMonths() {
        return ageInMonths;
    }

    public void setAgeInMonths(Integer ageInMonths) {
        this.ageInMonths = ageInMonths;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getAllergyInfo() {
        return allergyInfo;
    }

    public void setAllergyInfo(String allergyInfo) {
        this.allergyInfo = allergyInfo;
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
}
