package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMCallback;
import com.sd.lib.imsdk.handler.IMSender;

public class IMSenderEmpty implements IMSender
{
    @Override
    public void sendMessage(SendMessageRequest request, IMCallback<IMMessage> callback)
    {
        callback.onError(-1, "empty implementation");
    }
}
