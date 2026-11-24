package com.lidexin.business.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 婴幼儿请求DTO
 *
 * @author 张三
 * @since 2025-12-23
 */
@ApiModel(description = "婴幼儿请求参数")
public class BabyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "婴幼儿ID（修改时必填）")
    private Long id;

    @ApiModelProperty(value = "宝宝姓名", required = true)
    @NotBlank(message = "宝宝姓名不能为空")
    private String name;

    @ApiModelProperty(value = "宝宝昵称")
    private String nickname;

    @ApiModelProperty(value = "性别（0-未知，1-男，2-女）")
    private Byte gender;

    @ApiModelProperty(value = "出生日期", required = true)
    @NotNull(message = "出生日期不能为空")
    private LocalDate birthDate;

    @ApiModelProperty(value = "宝宝头像")
    private String avatar;

    @ApiModelProperty(value = "血型")
    private String bloodType;

    @ApiModelProperty(value = "过敏信息")
    private String allergyInfo;

    @ApiModelProperty(value = "备注")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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
}
