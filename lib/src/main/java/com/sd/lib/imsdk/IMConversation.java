package com.sd.lib.imsdk;

import com.sd.lib.imsdk.callback.IMCallback;
import com.sd.lib.imsdk.handler.IMConversationHandler;
import com.sd.lib.imsdk.handler.IMMessageSender;

import java.util.List;

public class IMConversation
{
    String peer;
    IMConversationType type;

    IMMessage lastMessage;

    public String getPeer()
    {
        return peer;
    }

    public IMConversationType getType()
    {
        return type;
    }

    /**
     * 刷新会话
     */
    public void load()
    {
        lastMessage = getLastMessage();
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
        holder.getMessagePersistence().saveMessage(message, type);

        final IMMessageSender.SendMessageRequest request = new IMMessageSender.SendMessageRequest(type, message, message.persistenceAccessor());
        holder.getMessageSender().sendMessage(request, callback);


        return message;
    }

    //---------- IMConversationHandler start ----------

    /**
     * 未读数量
     *
     * @return
     */
    public int getUnreadCount()
    {
        final IMConversationHandler handler = IMManager.getInstance().getHandlerHolder().getConversationHandler();
        return handler.getUnreadCount(this);
    }

    /**
     * 获取会话消息
     *
     * @param count
     * @param lastMsg
     * @param callback
     */
    public void getMessage(int count, IMMessage lastMsg, IMCallback<List<IMMessage>> callback)
    {
        final IMConversationHandler handler = IMManager.getInstance().getHandlerHolder().getConversationHandler();
        handler.getMessage(this, count, lastMsg, callback);
    }

    /**
     * 获取最后一条消息
     */
    public IMMessage getLastMessage()
    {
        final IMConversationHandler handler = IMManager.getInstance().getHandlerHolder().getConversationHandler();
        return handler.getLastMessage(this);
    }

    //---------- IMConversationHandler end ----------
}
