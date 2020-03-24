package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMCallback;
import com.sd.lib.imsdk.constant.IMCode;
import com.sd.lib.imsdk.handler.IMMessageSender;

public class IMMessageSenderEmpty implements IMMessageSender
{
    @Override
    public void sendMessage(IMMessage message, IMCallback callback)
    {
        callback.onError(IMCode.ERROR_OTHER, "empty implementation");
    }
}
