package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMCallback;
import com.sd.lib.imsdk.handler.IMConversationHandler;

import java.util.List;

public class IMConversationHandlerEmpty implements IMConversationHandler
{
    @Override
    public int getUnreadCount(IMConversation conversation)
    {
        return 0;
    }

    @Override
    public void getMessage(IMConversation conversation, int count, IMMessage lastMsg, IMCallback<List<IMMessage>> callback)
    {
        if (callback != null)
            callback.onError(-1, "empty implementation");
    }

    @Override
    public IMMessage getLastMessage(IMConversation conversation)
    {
        return null;
    }
}
