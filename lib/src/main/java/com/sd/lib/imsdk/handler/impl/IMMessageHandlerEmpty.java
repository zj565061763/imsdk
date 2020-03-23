package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.IMMessageState;
import com.sd.lib.imsdk.handler.IMMessageHandler;

public class IMMessageHandlerEmpty implements IMMessageHandler
{
    @Override
    public void interceptNewMessageSend(IMMessage.InterceptAccessor accessor)
    {

    }

    @Override
    public void saveMessage(IMMessage message)
    {

    }

    @Override
    public void updateMessageState(IMMessage message, IMMessageState state)
    {

    }
}
