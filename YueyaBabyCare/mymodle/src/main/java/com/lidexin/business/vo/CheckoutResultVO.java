package com.lidexin.business.vo;

/**
 * 签退结果VO
 */
public class CheckoutResultVO {

    /**
     * 签退记录ID
     */
    private Long checkoutId;

    /**
     * 提示消息（如：护理计划剩余天数提示）
     */
    private String message;

    public CheckoutResultVO() {
    }

    public CheckoutResultVO(Long checkoutId, String message) {
        this.checkoutId = checkoutId;
        this.message = message;
    }

    public Long getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(Long checkoutId) {
        this.checkoutId = checkoutId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
