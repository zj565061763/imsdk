package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.handler.IMMessageHandler;

class IMMessageHandlerEmpty implements IMMessageHandler
{
    @Override
    public void interceptNewMessageSend(IMMessage.Accessor accessor) throws Exception
    {

    }

    @Override
    public void saveMessage(IMMessage message) throws Exception
    {

    }

    @Override
    public void updateMessageState(IMMessage message) throws Exception
    {

    }

    @Override
    public void updateMessageItem(IMMessage message) throws Exception
    {

    }

    @Override
    public void updateMessageSender(IMMessage message) throws Exception
    {

    }

    @Override
    public void removeMessage(IMMessage message) throws Exception
    {

    }

    @Override
    public void checkInterruptedMessage() throws Exception
    {

    }
}
