package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMMessage;

public interface IMMessageHandler
{
    void interceptNewMessageSend(IMMessage.InterceptAccessor accessor);

    void saveMessage(IMMessage message);

    void removeMessage(IMMessage message);

    void checkInterruptedMessage();
}
