package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.IMMessageState;

public interface IMMessageHandler
{
    void interceptNewMessageSend(IMMessage.InterceptAccessor accessor);

    void saveMessage(IMMessage message, String itemContent);

    void updateMessageState(IMMessage message, IMMessageState state);
}
