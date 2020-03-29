package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.model.IMUser;

public interface IMMessageHandler
{
    void interceptNewMessageSend(IMMessage.Accessor accessor) throws Exception;

    void saveMessage(IMMessage message) throws Exception;

    void updateMessageState(IMMessage message) throws Exception;

    void updateMessageItem(IMMessage message) throws Exception;

    void updateMessageSender(String peer, IMConversationType conversationType, IMUser sender) throws Exception;

    void removeMessage(IMMessage message) throws Exception;

    void checkInterruptedMessage() throws Exception;
}
