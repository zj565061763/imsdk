package com.sd.lib.imsdk;

import com.sd.lib.imsdk.callback.IMCallback;
import com.sd.lib.imsdk.callback.IMOutgoingCallback;
import com.sd.lib.imsdk.callback.IMSendCallback;
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
        handler.load(peer, type, new PersistenceAccessor());
        return this;
    }

    /**
     * 发送消息
     *
     * @param item
     * @param callback
     * @return
     */
    public IMMessage send(IMMessageItem item, final IMSendCallback callback)
    {
        if (item == null)
            throw new NullPointerException("item is null");

        final IMMessage message = IMFactory.newMessageSend();
        message.peer = peer;
        message.conversationType = type;
        message.state = IMMessageState.None;
        message.isSelf = true;
        message.isRead = true;
        message.item = item;
        item.message = message;

        return send(message, callback);
    }

    IMMessage send(final IMMessage message, final IMSendCallback callback)
    {
        final IMHandlerHolder holder = IMManager.getInstance().getHandlerHolder();
        final String itemContent = holder.getMessageItemSerializer().serialize(message.getItem());

        holder.getMessageHandler().saveMessage(message, itemContent);
        holder.getConversationHandler().saveConversation(message);

        final List<IMOutgoingCallback> listCallback = IMManager.getInstance().getListIMOutgoingCallback();

        final IMMessageSender.SendMessageRequest request = new IMMessageSender.SendMessageRequest(type, message, message.persistenceAccessor());
        holder.getMessageSender().sendMessage(request, new IMCallback<IMMessage>()
        {
            @Override
            public void onSuccess(final IMMessage value)
            {
                holder.getMessageHandler().updateMessageState(message, IMMessageState.SendSuccess);
                IMUtils.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (callback != null)
                            callback.onSuccess(value);

                        for (IMOutgoingCallback item : listCallback)
                        {
                            item.onSuccess(value);
                        }
                    }
                });
            }

            @Override
            public void onError(final int code, final String desc)
            {
                holder.getMessageHandler().updateMessageState(message, IMMessageState.SendFail);
                IMUtils.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (callback != null)
                            callback.onError(message, code, desc);

                        for (IMOutgoingCallback item : listCallback)
                        {
                            item.onError(message, code, desc);
                        }
                    }
                });
            }
        });

        holder.getMessageHandler().updateMessageState(message, IMMessageState.Sending);
        IMUtils.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                for (IMOutgoingCallback item : listCallback)
                {
                    item.onSend(message);
                }
            }
        });

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
        private PersistenceAccessor()
        {
        }

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
