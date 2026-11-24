package com.lidexin.business.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 消息请求DTO
 *
 * @author 张三
 * @since 2025-12-24
 */
@ApiModel(description = "消息请求参数")
public class MessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "消息ID（修改时必填）")
    private Long id;

    @ApiModelProperty(value = "接收人类型（1-家长，2-保姆，3-机构，4-管理员）", required = true)
    @NotNull(message = "接收人类型不能为空")
    private Byte receiverType;

    @ApiModelProperty(value = "接收人ID", required = true)
    @NotNull(message = "接收人ID不能为空")
    private Long receiverId;

    @ApiModelProperty(value = "消息类型（1-系统消息，2-护理提醒，3-留言反馈）")
    private Byte msgType;

    @ApiModelProperty(value = "消息标题")
    private String title;

    @ApiModelProperty(value = "消息内容", required = true)
    @NotBlank(message = "消息内容不能为空")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Byte getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(Byte receiverType) {
        this.receiverType = receiverType;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Byte getMsgType() {
        return msgType;
    }

    public void setMsgType(Byte msgType) {
        this.msgType = msgType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}