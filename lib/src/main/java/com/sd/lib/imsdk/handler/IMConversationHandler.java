package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMValueCallback;

import java.util.List;

public interface IMConversationHandler
{
    void saveConversation(IMConversation conversation);

    void removeConversation(IMConversation conversation);

    boolean loadConversation(IMConversation conversation, IMConversation.PersistenceAccessor accessor);

    List<IMConversation> getAllConversation();

    void loadMessageBefore(IMConversation conversation, int count, IMMessage lastMessage, IMValueCallback<List<IMMessage>> callback);
}
