package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.IMMessageState;

public interface IMMessagePersistence
{
    void saveMessage(IMMessage message, IMConversationType conversationType);

    void updateMessageState(String id, IMMessageState state, IMConversationType conversationType);
}
