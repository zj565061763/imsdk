package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMValueCallback;
import com.sd.lib.imsdk.constant.IMCode;
import com.sd.lib.imsdk.handler.IMConversationHandler;

import java.util.List;

public class IMConversationHandlerEmpty implements IMConversationHandler
{
    @Override
    public void saveConversation(String peer, IMConversationType conversationType, IMMessage message)
    {

    }

    @Override
    public void removeConversation(String peer, IMConversationType conversationType)
    {

    }

    @Override
    public boolean loadConversation(String peer, IMConversationType conversationType, IMConversation.PersistenceAccessor accessor)
    {
        return false;
    }

    @Override
    public void loadMessageBefore(String peer, IMConversationType conversationType, int count, IMMessage lastMessage, IMValueCallback<List<IMMessage>> callback)
    {
        callback.onError(IMCode.ERROR_OTHER, "empty implementation");
    }
}
