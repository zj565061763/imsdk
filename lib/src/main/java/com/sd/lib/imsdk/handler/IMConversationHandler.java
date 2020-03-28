package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMValueCallback;

import java.util.List;

public interface IMConversationHandler
{
    void saveConversation(IMConversation conversation) throws Exception;

    void updateConversationExt(IMConversation conversation) throws Exception;

    void removeConversation(IMConversation conversation) throws Exception;

    void loadConversation(IMConversation conversation, IMConversation.Accessor accessor) throws Exception;

    int loadUnreadCount(IMConversation conversation) throws Exception;

    void setMessageRead(IMConversation conversation) throws Exception;

    List<IMConversation> getAllConversation() throws Exception;

    void loadMessageBefore(IMConversation conversation, int count, IMMessage lastMessage, IMValueCallback<List<IMMessage>> callback) throws Exception;
}
