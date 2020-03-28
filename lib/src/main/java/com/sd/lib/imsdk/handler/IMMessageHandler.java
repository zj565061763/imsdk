package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMMessage;

public interface IMMessageHandler
{
    void interceptNewMessageSend(IMMessage.Accessor accessor) throws Exception;

    void saveMessage(IMMessage message) throws Exception;

    void updateMessageState(IMMessage message) throws Exception;

    void updateMessageItem(IMMessage message) throws Exception;

    void removeMessage(IMMessage message) throws Exception;

    void checkInterruptedMessage() throws Exception;
}
