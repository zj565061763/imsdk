package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMValueCallback;

import java.util.List;

public interface IMConversationHandler
{
    void saveConversation(String peer, IMConversationType conversationType, IMMessage message);

    void removeConversation(String peer, IMConversationType conversationType);

    boolean loadConversation(String peer, IMConversationType conversationType, IMConversation.PersistenceAccessor accessor);

    void loadMessageBefore(String peer, IMConversationType conversationType, int count, IMMessage lastMessage, IMValueCallback<List<IMMessage>> callback);
}
