package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMHandlerHolder;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.handler.IMMessageHandler;
import com.sd.lib.imsdk.model.IMUser;

public class IMMessageHandlerWrapper implements IMMessageHandler
{
    private final IMMessageHandler mOriginal;
    private final IMHandlerHolder.CallbackHandler mCallbackHandler;

    public IMMessageHandlerWrapper(IMMessageHandler original, IMHandlerHolder.CallbackHandler callbackHandler)
    {
        if (original == null)
            original = new IMMessageHandlerEmpty();

        mOriginal = original;
        mCallbackHandler = callbackHandler;
    }

    @Override
    public void interceptNewMessageSend(IMMessage.Accessor accessor)
    {
        try
        {
            mOriginal.interceptNewMessageSend(accessor);
        } catch (Exception e)
        {
            mCallbackHandler.notifyOtherException("error interceptNewMessageSend", e);
        }
    }

    @Override
    public void saveMessage(IMMessage message)
    {
        try
        {
            mOriginal.saveMessage(message);
        } catch (Exception e)
        {
            mCallbackHandler.notifyOtherException("error saveMessage id:" + message.getId(), e);
        }
    }

    @Override
    public void updateMessageState(IMMessage message)
    {
        try
        {
            mOriginal.updateMessageState(message);
        } catch (Exception e)
        {
            mCallbackHandler.notifyOtherException("error updateMessageState id:" + message.getId(), e);
        }
    }

    @Override
    public void updateMessageItem(IMMessage message)
    {
        try
        {
            mOriginal.updateMessageItem(message);
        } catch (Exception e)
        {
            mCallbackHandler.notifyOtherException("error updateMessageItem id:" + message.getId(), e);
        }
    }

    @Override
    public void updateMessageSender(IMUser sender)
    {
        try
        {
            mOriginal.updateMessageSender(sender);
        } catch (Exception e)
        {
            mCallbackHandler.notifyOtherException("error updateMessageSender id:" + sender.getId(), e);
        }
    }

    @Override
    public void deleteMessage(IMMessage message)
    {
        try
        {
            mOriginal.deleteMessage(message);
        } catch (Exception e)
        {
            mCallbackHandler.notifyOtherException("error deleteMessage id:" + message.getId(), e);
        }
    }

    @Override
    public void checkInterruptedMessage()
    {
        try
        {
            mOriginal.checkInterruptedMessage();
        } catch (Exception e)
        {
            mCallbackHandler.notifyOtherException("error checkInterruptedMessage", e);
        }
    }
}
