package com.lidexin.business.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 可选联系人响应VO
 *
 * @author 张三
 * @since 2025-12-25
 */
@ApiModel(description = "可选联系人信息")
public class AvailableContactVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "联系人类型（1-家长，2-保姆，3-机构，4-管理员）")
    private Byte contactType;

    @ApiModelProperty(value = "联系人类型名称")
    private String contactTypeName;

    @ApiModelProperty(value = "联系人ID")
    private Long contactId;

    @ApiModelProperty(value = "联系人名称")
    private String contactName;

    @ApiModelProperty(value = "联系人头像")
    private String contactAvatar;

    @ApiModelProperty(value = "联系人手机号")
    private String contactPhone;

    @ApiModelProperty(value = "业务关系描述（如：有3个预约记录）")
    private String relationDesc;

    public Byte getContactType() {
        return contactType;
    }

    public void setContactType(Byte contactType) {
        this.contactType = contactType;
        // 设置类型名称
        if (contactType != null) {
            switch (contactType) {
                case 1:
                    this.contactTypeName = "家长";
                    break;
                case 2:
                    this.contactTypeName = "保姆";
                    break;
                case 3:
                    this.contactTypeName = "机构";
                    break;
                case 4:
                    this.contactTypeName = "管理员";
                    break;
                default:
                    this.contactTypeName = "未知";
            }
        }
    }

    public String getContactTypeName() {
        return contactTypeName;
    }

    public void setContactTypeName(String contactTypeName) {
        this.contactTypeName = contactTypeName;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactAvatar() {
        return contactAvatar;
    }

    public void setContactAvatar(String contactAvatar) {
        this.contactAvatar = contactAvatar;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getRelationDesc() {
        return relationDesc;
    }

    public void setRelationDesc(String relationDesc) {
        this.relationDesc = relationDesc;
    }
}