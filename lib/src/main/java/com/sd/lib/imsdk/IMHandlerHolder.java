package com.sd.lib.imsdk;

import com.sd.lib.imsdk.handler.IMConversationHandler;
import com.sd.lib.imsdk.handler.IMJsonSerializer;
import com.sd.lib.imsdk.handler.IMMessageHandler;
import com.sd.lib.imsdk.handler.IMMessageItemSerializer;
import com.sd.lib.imsdk.handler.IMMessageSender;
import com.sd.lib.imsdk.handler.IMPersistenceHandler;
import com.sd.lib.imsdk.handler.impl.IMConversationHandlerWrapper;
import com.sd.lib.imsdk.handler.impl.IMJsonSerializerGson;
import com.sd.lib.imsdk.handler.impl.IMMessageHandlerWrapper;
import com.sd.lib.imsdk.handler.impl.IMMessageItemSerializerGson;
import com.sd.lib.imsdk.handler.impl.IMMessageSenderEmpty;
import com.sd.lib.imsdk.handler.impl.IMPersistenceHandlerWrapper;

public class IMHandlerHolder
{
    private final CallbackHandler mCallbackHandler;

    private IMMessageItemSerializer mMessageItemSerializer;
    private IMMessageSender mMessageSender;
    private IMMessageHandlerWrapper mMessageHandler;
    private IMConversationHandlerWrapper mConversationHandler;
    private IMJsonSerializer mJsonSerializer;
    private IMPersistenceHandlerWrapper mPersistenceHandler;

    public IMHandlerHolder(CallbackHandler callbackHandler)
    {
        if (callbackHandler == null)
            throw new NullPointerException("callbackHandler is null");
        mCallbackHandler = callbackHandler;
    }

    public IMMessageItemSerializer getMessageItemSerializer()
    {
        if (mMessageItemSerializer == null)
            mMessageItemSerializer = new IMMessageItemSerializerGson();
        return mMessageItemSerializer;
    }

    public void setMessageItemSerializer(IMMessageItemSerializer serializer)
    {
        mMessageItemSerializer = serializer;
    }

    public IMMessageSender getMessageSender()
    {
        if (mMessageSender == null)
            mMessageSender = new IMMessageSenderEmpty();
        return mMessageSender;
    }

    public void setMessageSender(IMMessageSender sender)
    {
        mMessageSender = sender;
    }

    public IMMessageHandlerWrapper getMessageHandler()
    {
        if (mMessageHandler == null)
            mMessageHandler = new IMMessageHandlerWrapper(null, mCallbackHandler);
        return mMessageHandler;
    }

    public void setMessageHandler(IMMessageHandler handler)
    {
        mMessageHandler = new IMMessageHandlerWrapper(handler, mCallbackHandler);
    }

    public IMConversationHandlerWrapper getConversationHandler()
    {
        if (mConversationHandler == null)
            mConversationHandler = new IMConversationHandlerWrapper(null, mCallbackHandler);
        return mConversationHandler;
    }

    public void setConversationHandler(IMConversationHandler conversationHandler)
    {
        mConversationHandler = new IMConversationHandlerWrapper(conversationHandler, mCallbackHandler);
    }

    public IMJsonSerializer getJsonSerializer()
    {
        if (mJsonSerializer == null)
            mJsonSerializer = new IMJsonSerializerGson();
        return mJsonSerializer;
    }

    public void setJsonSerializer(IMJsonSerializer jsonSerializer)
    {
        mJsonSerializer = jsonSerializer;
    }

    public IMPersistenceHandlerWrapper getPersistenceHandler()
    {
        if (mPersistenceHandler == null)
            mPersistenceHandler = new IMPersistenceHandlerWrapper(null, mCallbackHandler);
        return mPersistenceHandler;
    }

    public void setPersistenceHandler(IMPersistenceHandler persistenceHandler)
    {
        mPersistenceHandler = new IMPersistenceHandlerWrapper(persistenceHandler, mCallbackHandler);
    }

    public interface CallbackHandler
    {
        void notifyOtherException(String message, Exception e);
    }
}
