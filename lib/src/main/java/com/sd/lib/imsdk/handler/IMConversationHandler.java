package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMCallback;

import java.util.List;

public interface IMConversationHandler
{
    void saveConversation(String peer, IMConversationType conversationType, IMMessage message);

    void removeConversation(String peer, IMConversationType conversationType);

    int getUnreadCount(IMConversation conversation);

    void getMessage(IMConversation conversation, int count, IMMessage lastMsg, IMCallback<List<IMMessage>> callback);

    IMMessage getLastMessage(IMConversation conversation);
}
