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
    long lastTimestamp;

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

    public long getLastTimestamp()
    {
        return lastTimestamp;
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
     * 加载会话
     *
     * @return true-加载成功；false-加载失败
     */
    public boolean load()
    {
        final IMConversationHandler handler = IMManager.getInstance().getHandlerHolder().getConversationHandler();
        return handler.loadConversation(this, new PersistenceAccessor());
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

        // 发送中
        message.state = IMMessageState.Sending;
        message.save();

        this.lastTimestamp = System.currentTimeMillis();
        holder.getConversationHandler().saveConversation(this, message);

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
                        message.save();
                        notifyCallbackSuccess(message, callback);
                    }

                    @Override
                    public void onError(int code, String desc)
                    {
                        message.state = IMMessageState.SendFail;
                        message.save();
                        notifyCallbackError(message, code, desc, callback);
                    }
                });
            }
        };

        final IMMessageItem messageItem = message.getItem();
        if (messageItem.isNeedUpload())
        {
            final boolean[] arrTag = new boolean[]{false};
            final boolean uploadSubmitted = messageItem.upload(new IMMessageItem.UploadCallback()
            {
                @Override
                public void onProgress(int progress)
                {
                    if (arrTag[0])
                        notifyCallbackProgress(message, messageItem, progress, callback);
                }

                @Override
                public void onSuccess()
                {
                    if (arrTag[0])
                    {
                        message.save();
                        sendRunnable.run();
                    }
                }

                @Override
                public void onError(String desc)
                {
                    if (arrTag[0])
                    {
                        message.state = IMMessageState.SendFail;
                        message.save();
                        notifyCallbackError(message, IMCode.ERROR_UPLOAD_ITEM, desc, callback);
                    }
                }
            });

            arrTag[0] = uploadSubmitted;
            if (!uploadSubmitted)
            {
                message.state = IMMessageState.SendFail;
                message.save();
                notifyCallbackError(message, IMCode.ERROR_UPLOAD_ITEM, "upload return false", callback);
            }
        } else
        {
            sendRunnable.run();
        }

        return message;
    }

    /**
     * 获取当前会话中指定消息之前的消息
     *
     * @param count
     * @param lastMessage
     * @param callback
     */
    public void loadMessageBefore(int count, IMMessage lastMessage, IMValueCallback<List<IMMessage>> callback)
    {
        final IMConversationHandler handler = IMManager.getInstance().getHandlerHolder().getConversationHandler();
        handler.loadMessageBefore(this, count, lastMessage, callback);
    }

    public final class PersistenceAccessor
    {
        private PersistenceAccessor()
        {
        }

        public void setLastTimestamp(long timestamp)
        {
            IMConversation.this.lastTimestamp = timestamp;
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
