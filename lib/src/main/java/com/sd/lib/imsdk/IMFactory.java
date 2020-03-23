package com.sd.lib.imsdk;

import com.sd.lib.imsdk.handler.IMMessageHandler;

import java.util.UUID;

public class IMFactory
{
    /**
     * 创建发送消息
     *
     * @return
     */
    static IMMessage newMessageSend()
    {
        final IMMessage message = new IMMessage();
        message.id = UUID.randomUUID().toString();
        message.timestamp = System.currentTimeMillis();
        message.sender = IMManager.getInstance().getLoginUser();

        final IMMessageHandler handler = IMManager.getInstance().getHandlerHolder().getMessageHandler();
        handler.interceptNewMessageSend(message.interceptAccessor());
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
        final IMConversation conversation = new IMConversation(peer, type);
        return conversation;
    }

    public static IMMessage newMessageQuery()
    {
        final IMMessage message = new IMMessage();
        return message;
    }
}
