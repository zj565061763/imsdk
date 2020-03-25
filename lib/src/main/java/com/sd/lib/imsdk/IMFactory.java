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
        final IMConversation conversation = new IMConversation();
        conversation.peer = peer;
        conversation.type = type;
        return conversation;
    }

    public static IMMessageQuery newMessageQuery()
    {
        final IMMessage message = new IMMessage();
        final IMMessageQuery messageQuery = new IMMessageQuery(message, message.persistenceAccessor());
        return messageQuery;
    }

    public static class IMMessageQuery
    {
        public final IMMessage message;
        public final IMMessage.PersistenceAccessor accessor;

        private IMMessageQuery(IMMessage message, IMMessage.PersistenceAccessor accessor)
        {
            this.message = message;
            this.accessor = accessor;
        }
    }
}
