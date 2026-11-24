package com.lidexin.business.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 消息/留言表
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发送人类型（1-家长，2-保姆，3-机构，4-管理员）
     */
    private Byte senderType;

    /**
     * 发送人ID
     */
    private Long senderId;

    /**
     * 接收人类型（1-家长，2-保姆，3-机构，4-管理员）
     */
    private Byte receiverType;

    /**
     * 接收人ID
     */
    private Long receiverId;

    /**
     * 消息类型（1-系统消息，2-护理提醒，3-留言反馈）
     */
    private Byte msgType;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 是否已读（0-未读，1-已读）
     */
    private Byte isRead;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

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

    public Byte getSenderType() {
        return senderType;
    }

    public void setSenderType(Byte senderType) {
        this.senderType = senderType;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
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

    public Byte getIsRead() {
        return isRead;
    }

    public void setIsRead(Byte isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getReadTime() {
        return readTime;
    }

    public void setReadTime(LocalDateTime readTime) {
        this.readTime = readTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Byte getDeleted() {
        return deleted;
    }

    public void setDeleted(Byte deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Message{" +
            "id = " + id +
            ", senderType = " + senderType +
            ", senderId = " + senderId +
            ", receiverType = " + receiverType +
            ", receiverId = " + receiverId +
            ", msgType = " + msgType +
            ", title = " + title +
            ", content = " + content +
            ", isRead = " + isRead +
            ", readTime = " + readTime +
            ", createTime = " + createTime +
            ", deleted = " + deleted +
        "}";
    }
}
