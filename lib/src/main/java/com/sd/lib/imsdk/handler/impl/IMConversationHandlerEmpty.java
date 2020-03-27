package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMValueCallback;
import com.sd.lib.imsdk.constant.IMCode;
import com.sd.lib.imsdk.handler.IMConversationHandler;

import java.util.List;

class IMConversationHandlerEmpty implements IMConversationHandler
{
    @Override
    public void saveConversation(IMConversation conversation) throws Exception
    {

    }

    @Override
    public void saveConversationExt(IMConversation conversation) throws Exception
    {

    }

    @Override
    public void removeConversation(IMConversation conversation) throws Exception
    {

    }

    @Override
    public boolean loadConversation(IMConversation conversation, IMConversation.Accessor accessor) throws Exception
    {
        return false;
    }

    @Override
    public List<IMConversation> getAllConversation() throws Exception
    {
        return null;
    }

    @Override
    public void loadMessageBefore(IMConversation conversation, int count, IMMessage lastMessage, IMValueCallback<List<IMMessage>> callback) throws Exception
    {
        callback.onError(IMCode.ERROR_OTHER, "empty implementation");
    }
}
