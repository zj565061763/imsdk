package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMCallback;

import java.util.List;

public interface IMConversationHandler
{
    void saveConversation(IMMessage message);

    void removeConversation(String peer, IMConversationType conversationType);

    void load(IMConversation.PersistenceAccessor accessor);

    void loadMessage(IMConversation conversation, int count, IMMessage lastMessage, IMCallback<List<IMMessage>> callback);
}
