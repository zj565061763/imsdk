package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMHandlerHolder;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMValueCallback;
import com.sd.lib.imsdk.handler.IMConversationHandler;

import java.util.List;

public class IMConversationHandlerWrapper implements IMConversationHandler
{
    private final IMConversationHandler mOriginal;
    private final IMHandlerHolder.CallbackHandler mCallbackHandler;

    public IMConversationHandlerWrapper(IMConversationHandler original, IMHandlerHolder.CallbackHandler callbackHandler)
    {
        if (original == null)
            original = new IMConversationHandlerEmpty();

        mOriginal = original;
        mCallbackHandler = callbackHandler;
    }

    @Override
    public void saveConversation(IMConversation conversation)
    {
        try
        {
            mOriginal.saveConversation(conversation);
        } catch (Exception e)
        {
            mCallbackHandler.notifyOtherException("error saveConversation peer:" + conversation.getPeer() + " type:" + conversation.getType(), e);
        }
    }

    @Override
    public void saveConversationExt(IMConversation conversation)
    {
        try
        {
            mOriginal.saveConversationExt(conversation);
        } catch (Exception e)
        {
            mCallbackHandler.notifyOtherException("error saveConversationExt peer:" + conversation.getPeer() + " type:" + conversation.getType(), e);
        }
    }

    @Override
    public void removeConversation(IMConversation conversation)
    {
        try
        {
            mOriginal.removeConversation(conversation);
        } catch (Exception e)
        {
            mCallbackHandler.notifyOtherException("error removeConversation peer:" + conversation.getPeer() + " type:" + conversation.getType(), e);
        }
    }

    @Override
    public boolean loadConversation(IMConversation conversation, IMConversation.Accessor accessor)
    {
        try
        {
            return mOriginal.loadConversation(conversation, accessor);
        } catch (Exception e)
        {
            mCallbackHandler.notifyOtherException("error loadConversation peer:" + conversation.getPeer() + " type:" + conversation.getType(), e);
        }
        return false;
    }

    @Override
    public List<IMConversation> getAllConversation()
    {
        try
        {
            return mOriginal.getAllConversation();
        } catch (Exception e)
        {
            mCallbackHandler.notifyOtherException("error getAllConversation", e);
        }
        return null;
    }

    @Override
    public void loadMessageBefore(IMConversation conversation, int count, IMMessage lastMessage, IMValueCallback<List<IMMessage>> callback)
    {
        try
        {
            mOriginal.loadMessageBefore(conversation, count, lastMessage, callback);
        } catch (Exception e)
        {
            mCallbackHandler.notifyOtherException("error loadMessageBefore peer:" + conversation.getPeer() + " type:" + conversation.getType(), e);
        }
    }
}
