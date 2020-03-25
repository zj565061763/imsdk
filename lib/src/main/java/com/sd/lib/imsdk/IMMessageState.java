package com.sd.lib.imsdk;

public enum IMMessageState
{
    None,
    /**
     * 准备发送
     */
    SendPrepare,
    /**
     * 上传消息Item
     */
    UploadItem,
    /**
     * 发送中
     */
    Sending,
    /**
     * 发送成功
     */
    SendSuccess,
    /**
     * 发送失败
     */
    SendFail,
    /**
     * 接收的消息
     */
    Receive,
}
