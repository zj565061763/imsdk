package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMCallback;
import com.sd.lib.imsdk.handler.IMMessageSender;

public class IMMessageSenderEmpty implements IMMessageSender
{
    @Override
    public void sendMessage(SendMessageRequest request, IMCallback<IMMessage> callback)
    {
        callback.onError(-1, "empty implementation");
    }
}
