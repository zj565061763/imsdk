package com.sd.lib.imsdk;

import com.sd.lib.imsdk.handler.IMMessageHandler;

import java.util.UUID;

class IMFactory
{
    public static IMMessage newMessageSend()
    {
        final IMMessage message = new IMMessage();
        message.id = UUID.randomUUID().toString();
        message.timestamp = System.currentTimeMillis();
        message.sender = IMManager.getInstance().getLoginUser();

        final IMMessageHandler handler = IMManager.getInstance().getHandlerHolder().getMessageHandler();
        handler.interceptNewMessageSend(message.interceptAccessor());
        return message;
    }

    public static IMMessage newMessageReceive()
    {
        final IMMessage message = new IMMessage();
        return message;
    }

    public static IMConversation newConversation(String peer, IMConversationType type)
    {
        final IMConversation conversation = new IMConversation();
        conversation.peer = peer;
        conversation.type = type;
        return conversation;
    }
}
