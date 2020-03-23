package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMConversationType;
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
    public void saveMessage(IMMessage message, IMConversationType conversationType)
    {

    }

    @Override
    public void updateMessageState(String id, IMMessageState state, IMConversationType conversationType)
    {

    }
}
