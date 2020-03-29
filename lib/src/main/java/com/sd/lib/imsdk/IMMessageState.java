package com.sd.lib.imsdk;

public enum IMMessageState
{
    none,
    /**
     * 准备发送
     */
    send_prepare,
    /**
     * 上传消息Item
     */
    upload_item,
    /**
     * 发送中
     */
    sending,
    /**
     * 发送成功
     */
    send_success,
    /**
     * 发送失败
     */
    send_fail,
    /**
     * 接收的消息
     */
    receive,
}
