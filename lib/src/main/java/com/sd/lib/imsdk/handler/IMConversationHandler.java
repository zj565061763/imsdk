package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMValueCallback;

import java.util.List;

public interface IMConversationHandler
{
    void saveConversation(IMMessage message);

    void removeConversation(String peer, IMConversationType conversationType);

    void load(String peer, IMConversationType conversationType, IMConversation.PersistenceAccessor accessor);

    void loadMessage(IMConversation conversation, int count, IMMessage lastMessage, IMValueCallback<List<IMMessage>> callback);
}
