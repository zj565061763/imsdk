package com.sd.lib.imsdk;

public enum IMMessageState
{
    None,
    /**
     * 发送失败
     */
    SendFail,
    /**
     * 发送中
     */
    Sending,
    /**
     * 发送成功
     */
    SendSuccess,
    /**
     * 接收的消息
     */
    Receive,
}
