package com.sd.lib.imsdk;

import com.sd.lib.imsdk.handler.IMFactoryHandler;

import java.util.UUID;

class IMFactory
{
    public static IMMessage newMessageSend(IMMessageItem item)
    {
        final IMMessage message = new IMMessage();
        message.id = UUID.randomUUID().toString();
        message.timestamp = System.currentTimeMillis();
        message.item = item;
        message.sender = IMManager.getInstance().getLoginUser();
        item.message = message;

        final IMFactoryHandler factoryHandler = IMManager.getInstance().getHandlerHolder().getFactoryHandler();
        factoryHandler.interceptNewMessageSend(message.interceptAccessor());
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
