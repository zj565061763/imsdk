package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMMessage;

public interface IMFactoryHandler
{
    void interceptNewMessageSend(IMMessage.InterceptAccessor accessor);
}
