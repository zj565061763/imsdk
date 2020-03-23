package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.handler.IMConversationPersistence;

public class IMConversationPersistenceEmpty implements IMConversationPersistence
{
    @Override
    public void saveConversation(IMMessage message, IMConversationType conversationType)
    {

    }

    @Override
    public void removeConversation()
    {

    }
}
