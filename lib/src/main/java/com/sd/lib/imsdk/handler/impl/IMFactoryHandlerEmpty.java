package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.handler.IMFactoryHandler;

public class IMFactoryHandlerEmpty implements IMFactoryHandler
{
    @Override
    public void interceptNewMessageSend(IMMessage.InterceptAccessor accessor)
    {

    }
}
