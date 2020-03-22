package com.sd.lib.imsdk;

import com.sd.lib.imsdk.handler.IMConversationHandler;
import com.sd.lib.imsdk.handler.IMFactoryHandler;
import com.sd.lib.imsdk.handler.IMMessageItemSerializer;
import com.sd.lib.imsdk.handler.IMMessagePersistence;
import com.sd.lib.imsdk.handler.IMSender;
import com.sd.lib.imsdk.handler.IMUserProvider;
import com.sd.lib.imsdk.handler.impl.IMConversationHandlerEmpty;
import com.sd.lib.imsdk.handler.impl.IMFactoryHandlerEmpty;
import com.sd.lib.imsdk.handler.impl.IMMessageItemSerializerGson;
import com.sd.lib.imsdk.handler.impl.IMMessagePersistenceEmpty;
import com.sd.lib.imsdk.handler.impl.IMSenderEmpty;
import com.sd.lib.imsdk.handler.impl.IMUserProviderEmpty;

public class IMHandlerHolder
{
    private IMFactoryHandler mFactoryHandler;
    private IMSender mSender;
    private IMMessagePersistence mMessagePersistence;
    private IMUserProvider mUserProvider;
    private IMConversationHandler mConversationHandler;
    private IMMessageItemSerializer mMessageItemSerializer;

    public IMFactoryHandler getFactoryHandler()
    {
        if (mFactoryHandler == null)
            mFactoryHandler = new IMFactoryHandlerEmpty();
        return mFactoryHandler;
    }

    public void setFactoryHandler(IMFactoryHandler factoryHandler)
    {
        mFactoryHandler = factoryHandler;
    }

    public IMSender getSender()
    {
        if (mSender == null)
            mSender = new IMSenderEmpty();
        return mSender;
    }

    public void setSender(IMSender sender)
    {
        mSender = sender;
    }

    public IMMessagePersistence getMessagePersistence()
    {
        if (mMessagePersistence == null)
            mMessagePersistence = new IMMessagePersistenceEmpty();
        return mMessagePersistence;
    }

    public void setMessagePersistence(IMMessagePersistence persistence)
    {
        mMessagePersistence = persistence;
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
}
