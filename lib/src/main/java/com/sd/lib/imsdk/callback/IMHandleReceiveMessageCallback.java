package com.sd.lib.imsdk.callback;

import com.sd.lib.imsdk.IMMessage;

public interface IMHandleReceiveMessageCallback
{
    /**
     * 消息被创建
     *
     * @param message
     */
    void onCreate(IMMessage message);
}
