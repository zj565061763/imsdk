package com.sd.lib.imsdk.callback;

import com.sd.lib.imsdk.IMMessage;

public interface IMHandleReceiveMessageCallback
{
    /**
     * 消息被创建
     *
     * @param message
     * @param accessor
     */
    void onCreate(IMMessage message, IMMessage.Accessor accessor);
}
