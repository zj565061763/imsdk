package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.IMMessage;

public interface IMConversationPersistence
{
    void saveConversation(String peer, IMConversationType conversationType, IMMessage message);

    void removeConversation(String peer, IMConversationType conversationType);
}
