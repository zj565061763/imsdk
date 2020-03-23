package com.sd.lib.imsdk;

import com.sd.lib.imsdk.callback.IMCallback;
import com.sd.lib.imsdk.handler.IMConversationHandler;
import com.sd.lib.imsdk.handler.IMMessageSender;

import java.util.List;

public class IMConversation
{
    private final String peer;
    private final IMConversationType type;

    IMMessage lastMessage;
    int unreadCount;

    IMConversation(String peer, IMConversationType type)
    {
        this.peer = peer;
        this.type = type;
    }

    public String getPeer()
    {
        return peer;
    }

    public IMConversationType getType()
    {
        return type;
    }

    public IMMessage getLastMessage()
    {
        return lastMessage;
    }

    public int getUnreadCount()
    {
        return unreadCount;
    }

    /**
     * 刷新会话
     *
     * @return
     */
    public IMConversation load()
    {
        final IMConversationHandler handler = IMManager.getInstance().getHandlerHolder().getConversationHandler();
        handler.load(new PersistenceAccessor());
        return this;
    }

    /**
     * 发送消息
     *
     * @param item
     * @param callback
     * @return
     */
    public IMMessage send(IMMessageItem item, IMCallback<IMMessage> callback)
    {
        if (item == null)
            throw new NullPointerException("item is null");

        final IMHandlerHolder holder = IMManager.getInstance().getHandlerHolder();

        final IMMessage message = IMFactory.newMessageSend();
        message.state = IMMessageState.None;
        message.isSelf = true;
        message.peer = peer;
        message.item = item;
        item.message = message;

        holder.getConversationHandler().saveConversation(peer, type, message);
        holder.getMessageHandler().saveMessage(message, type);

        final IMMessageSender.SendMessageRequest request = new IMMessageSender.SendMessageRequest(type, message, message.persistenceAccessor());
        holder.getMessageSender().sendMessage(request, callback);


        return message;
    }

    /**
     * 获取会话消息
     *
     * @param count
     * @param lastMessage
     * @param callback
     */
    public void loadMessage(int count, IMMessage lastMessage, IMCallback<List<IMMessage>> callback)
    {
        final IMConversationHandler handler = IMManager.getInstance().getHandlerHolder().getConversationHandler();
        handler.loadMessage(this, count, lastMessage, callback);
    }

    public final class PersistenceAccessor
    {
        public void setLastMessage(IMMessage message)
        {
            IMConversation.this.lastMessage = message;
        }

        public void setUnreadCount(int count)
        {
            IMConversation.this.unreadCount = count;
        }
    }
}
