package com.sd.lib.imsdk;

import com.sd.lib.imsdk.handler.impl.IMMessageHandlerWrapper;

import java.util.UUID;

class IMFactory
{
    /**
     * 创建发送消息
     *
     * @return
     */
    static IMMessage newMessageSend()
    {
        final IMMessage message = new IMMessage();
        message.setId(UUID.randomUUID().toString());
        message.setTimestamp(System.currentTimeMillis());

        final IMMessageHandlerWrapper handler = IMManager.getInstance().getHandlerHolder().getMessageHandler();
        handler.interceptNewMessageSend(message.accessor());
        return message;
    }

    /**
     * 创建接收消息
     *
     * @return
     */
    static IMMessage newMessageReceive()
    {
        final IMMessage message = new IMMessage();
        return message;
    }

    /**
     * 创建会话
     *
     * @param peer
     * @param type
     * @return
     */
    static IMConversation newConversation(String peer, IMConversationType type)
    {
        final IMConversation conversation = new IMConversation();
        conversation.peer = peer;
        conversation.type = type;
        return conversation;
    }
}
