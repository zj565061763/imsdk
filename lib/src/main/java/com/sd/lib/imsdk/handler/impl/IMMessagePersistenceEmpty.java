package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.IMMessageState;
import com.sd.lib.imsdk.handler.IMMessagePersistence;

public class IMMessagePersistenceEmpty implements IMMessagePersistence
{
    @Override
    public void saveMessage(IMMessage message, IMConversationType conversationType)
    {

    }

    @Override
    public void updateMessageState(String id, IMMessageState state, IMConversationType conversationType)
    {

    }
}
