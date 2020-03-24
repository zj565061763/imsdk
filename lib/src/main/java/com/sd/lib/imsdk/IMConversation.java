package com.sd.lib.imsdk;

import com.sd.lib.imsdk.callback.IMCallback;
import com.sd.lib.imsdk.callback.IMOutgoingCallback;
import com.sd.lib.imsdk.callback.IMSendCallback;
import com.sd.lib.imsdk.callback.IMValueCallback;
import com.sd.lib.imsdk.constant.IMCode;
import com.sd.lib.imsdk.handler.IMConversationHandler;

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
        handler.loadConversation(peer, type, new PersistenceAccessor());
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

        final IMMessageItem messageItem = message.getItem();
        final String itemContent = holder.getMessageItemSerializer().serialize(messageItem);

        holder.getMessageHandler().saveMessage(message, itemContent);
        holder.getConversationHandler().saveConversation(message);

        // 发送中
        message.state = IMMessageState.Sending;
        holder.getMessageHandler().updateMessageState(message, IMMessageState.Sending);
        IMUtils.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                final List<IMOutgoingCallback> listCallback = IMManager.getInstance().getListIMOutgoingCallback();
                for (IMOutgoingCallback item : listCallback)
                {
                    item.onSend(message);
                }
            }
        });

        final Runnable sendRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                holder.getMessageSender().sendMessage(message, new IMCallback()
                {
                    @Override
                    public void onSuccess()
                    {
                        message.state = IMMessageState.SendSuccess;
                        holder.getMessageHandler().updateMessageState(message, IMMessageState.SendSuccess);
                        notifyCallbackSuccess(message, callback);
                    }

                    @Override
                    public void onError(int code, String desc)
                    {
                        message.state = IMMessageState.SendFail;
                        holder.getMessageHandler().updateMessageState(message, IMMessageState.SendFail);
                        notifyCallbackError(message, code, desc, callback);
                    }
                });
            }
        };

        if (messageItem.isNeedUpload())
        {
            messageItem.upload(new IMMessageItem.UploadCallback()
            {
                @Override
                public void onProgress(int progress)
                {
                    notifyCallbackProgress(message, messageItem, progress, callback);
                }

                @Override
                public void onSuccess()
                {
                    message.save();
                    sendRunnable.run();
                }

                @Override
                public void onError(String desc)
                {
                    message.state = IMMessageState.SendFail;
                    holder.getMessageHandler().updateMessageState(message, IMMessageState.SendFail);
                    notifyCallbackError(message, IMCode.ERROR_UPLOAD_ITEM, desc, callback);
                }
            });
        } else
        {
            sendRunnable.run();
        }

        return message;
    }

    /**
     * 获取会话消息
     *
     * @param count
     * @param lastMessage
     * @param callback
     */
    public void loadMessage(int count, IMMessage lastMessage, IMValueCallback<List<IMMessage>> callback)
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

    private static void notifyCallbackProgress(final IMMessage message, final IMMessageItem messageItem, final int progress, final IMSendCallback callback)
    {
        IMUtils.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                    callback.onProgress(message, messageItem, progress);

                final List<IMOutgoingCallback> listCallback = IMManager.getInstance().getListIMOutgoingCallback();
                for (IMOutgoingCallback item : listCallback)
                {
                    item.onProgress(message, messageItem, progress);
                }
            }
        });
    }

    private static void notifyCallbackError(final IMMessage message, final int code, final String desc, final IMSendCallback callback)
    {
        IMUtils.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                    callback.onError(message, code, desc);

                final List<IMOutgoingCallback> listCallback = IMManager.getInstance().getListIMOutgoingCallback();
                for (IMOutgoingCallback item : listCallback)
                {
                    item.onError(message, code, desc);
                }
            }
        });
    }

    private static void notifyCallbackSuccess(final IMMessage message, final IMSendCallback callback)
    {
        IMUtils.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                    callback.onSuccess(message);

                final List<IMOutgoingCallback> listCallback = IMManager.getInstance().getListIMOutgoingCallback();
                for (IMOutgoingCallback item : listCallback)
                {
                    item.onSuccess(message);
                }
            }
        });
    }
}
