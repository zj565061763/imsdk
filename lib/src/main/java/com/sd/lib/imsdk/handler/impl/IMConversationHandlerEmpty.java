package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMCallback;
import com.sd.lib.imsdk.handler.IMConversationHandler;

import java.util.List;

public class IMConversationHandlerEmpty implements IMConversationHandler
{
    @Override
    public void saveConversation(IMMessage message)
    {

    }

    @Override
    public void removeConversation(String peer, IMConversationType conversationType)
    {

    }

    @Override
    public void load(IMConversation.PersistenceAccessor accessor)
    {

    }

    @Override
    public void loadMessage(IMConversation conversation, int count, IMMessage lastMessage, IMCallback<List<IMMessage>> callback)
    {
        callback.onError(-1, "empty implementation");
    }
}
