package com.sd.lib.imsdk;

import com.sd.lib.imsdk.handler.IMConversationHandler;
import com.sd.lib.imsdk.handler.IMMessageItemSerializer;
import com.sd.lib.imsdk.handler.IMMessageHandler;
import com.sd.lib.imsdk.handler.IMMessageSender;
import com.sd.lib.imsdk.handler.IMUserProvider;
import com.sd.lib.imsdk.handler.impl.IMConversationHandlerEmpty;
import com.sd.lib.imsdk.handler.impl.IMMessageItemSerializerGson;
import com.sd.lib.imsdk.handler.impl.IMMessageHandlerEmpty;
import com.sd.lib.imsdk.handler.impl.IMMessageSenderEmpty;
import com.sd.lib.imsdk.handler.impl.IMUserProviderEmpty;

public class IMHandlerHolder
{
    private IMMessageItemSerializer mMessageItemSerializer;
    private IMMessageSender mMessageSender;
    private IMMessageHandler mMessageHandler;
    private IMConversationHandler mConversationHandler;
    private IMUserProvider mUserProvider;

    public IMMessageItemSerializer getMessageItemSerializer()
    {
        if (mMessageItemSerializer == null)
            mMessageItemSerializer = new IMMessageItemSerializerGson();
        return mMessageItemSerializer;
    }

    public void setMessageItemSerializer(IMMessageItemSerializer messageItemSerializer)
    {
        mMessageItemSerializer = messageItemSerializer;
    }

    public IMMessageSender getMessageSender()
    {
        if (mMessageSender == null)
            mMessageSender = new IMMessageSenderEmpty();
        return mMessageSender;
    }

    public void setMessageSender(IMMessageSender messageSender)
    {
        mMessageSender = messageSender;
    }

    public IMMessageHandler getMessageHandler()
    {
        if (mMessageHandler == null)
            mMessageHandler = new IMMessageHandlerEmpty();
        return mMessageHandler;
    }

    public void setMessageHandler(IMMessageHandler persistence)
    {
        mMessageHandler = persistence;
    }

    public IMConversationHandler getConversationHandler()
    {
        if (mConversationHandler == null)
            mConversationHandler = new IMConversationHandlerEmpty();
        return mConversationHandler;
    }

    public void setConversationHandler(IMConversationHandler conversationHandler)
    {
        mConversationHandler = conversationHandler;
    }

    public IMUserProvider getUserProvider()
    {
        if (mUserProvider == null)
            mUserProvider = new IMUserProviderEmpty();
        return mUserProvider;
    }

    public void setUserProvider(IMUserProvider provider)
    {
        mUserProvider = provider;
    }
}
