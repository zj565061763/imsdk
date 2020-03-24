package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMCallback;

public interface IMMessageSender
{
    void sendMessage(IMMessage message, IMCallback callback);
}
