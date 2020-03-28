package com.sd.lib.imsdk;

import com.sd.lib.imsdk.callback.IMCallback;
import com.sd.lib.imsdk.callback.IMOutgoingCallback;
import com.sd.lib.imsdk.callback.IMSendCallback;
import com.sd.lib.imsdk.callback.IMValueCallback;
import com.sd.lib.imsdk.constant.IMCode;
import com.sd.lib.imsdk.handler.impl.IMConversationHandlerWrapper;
import com.sd.lib.imsdk.model.IMConversationExt;
import com.sd.lib.imsdk.model.IMUser;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IMConversation
{
    String peer;
    IMConversationType type;

    IMMessage lastMessage;
    int unreadCount;
    long lastTimestamp;

    IMConversation()
    {
    }

    private IMConversationExt ext;

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

    public long getLastTimestamp()
    {
        return lastTimestamp;
    }

    public IMConversationExt getExt()
    {
        if (ext == null)
            ext = new IMConversationExt();
        return ext;
    }

    void read(IMConversation conversation)
    {
        if (!peer.equals(conversation.peer))
            throw new IllegalArgumentException("read conversation error peer");

        if (!type.equals(conversation.type))
            throw new IllegalArgumentException("read conversation error type");

        this.lastMessage = conversation.getLastMessage();
        this.unreadCount = conversation.getUnreadCount();
        this.lastTimestamp = conversation.getLastTimestamp();
        this.ext = conversation.getExt();
    }

    /**
     * 加载会话
     */
    public void load()
    {
        if (!IMManager.getInstance().isLogin())
            return;

        final IMConversationHandlerWrapper handler = IMManager.getInstance().getHandlerHolder().getConversationHandler();
        handler.loadConversation(this, accessor());
    }

    /**
     * 加载未读数量
     */
    public int loadUnreadCount()
    {
        if (!IMManager.getInstance().isLogin())
            return 0;

        final IMConversationHandlerWrapper handler = IMManager.getInstance().getHandlerHolder().getConversationHandler();
        final int unreadCount = handler.loadUnreadCount(this);
        this.unreadCount = unreadCount;
        return this.unreadCount;
    }

    /**
     * 将会话的所有消息设置为已读
     */
    public void setMessageRead()
    {
        if (!IMManager.getInstance().isLogin())
            return;

        final IMConversationHandlerWrapper handler = IMManager.getInstance().getHandlerHolder().getConversationHandler();
        handler.setMessageRead(this);
        this.unreadCount = 0;
    }

    /**
     * 保存会话
     */
    public void save()
    {
        IMManager.getInstance().getHandlerHolder().getConversationHandler().saveConversation(this);
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

        if (item.isEmpty())
        {
            if (callback != null)
                callback.onError(null, IMCode.ERROR_EMPTY_ITEM, "empty message item");
            return null;
        }

        final IMUser loginUser = IMManager.getInstance().getLoginUser();
        if (loginUser == null)
        {
            if (callback != null)
                callback.onError(null, IMCode.ERROR_NOT_LOGIN, "not login");
            return null;
        }

        final IMMessage message = IMFactory.newMessageSend();
        message.peer = peer;
        message.conversationType = type;
        message.isSelf = true;
        message.isRead = true;
        message.item = item;
        message.sender = loginUser;
        item.message = message;

        return sendInternal(message, callback);
    }

    private IMMessage sendInternal(final IMMessage message, final IMSendCallback callback)
    {
        final IMHandlerHolder holder = IMManager.getInstance().getHandlerHolder();

        message.state = IMMessageState.SendPrepare;
        message.save();

        this.lastTimestamp = System.currentTimeMillis();
        this.lastMessage = message;
        IMManager.getInstance().saveConversationLocal(this);

        IMUtils.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                final Collection<IMOutgoingCallback> listCallback = IMManager.getInstance().getListIMOutgoingCallback();
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
                message.state = IMMessageState.Sending;
                holder.getMessageHandler().updateMessageState(message);

                holder.getMessageSender().sendMessage(message, new IMCallback()
                {
                    @Override
                    public void onSuccess()
                    {
                        message.state = IMMessageState.SendSuccess;
                        holder.getMessageHandler().updateMessageState(message);
                        notifyCallbackSuccess(message, callback);
                    }

                    @Override
                    public void onError(int code, String desc)
                    {
                        message.state = IMMessageState.SendFail;
                        holder.getMessageHandler().updateMessageState(message);
                        notifyCallbackError(message, code, desc, callback);
                    }
                });
            }
        };

        final IMMessageItem messageItem = message.getItem();
        if (messageItem.isNeedUpload())
        {
            message.state = IMMessageState.UploadItem;
            holder.getMessageHandler().updateMessageState(message);

            messageItem.upload(new IMMessageItem.UploadCallback()
            {
                @Override
                public void onProgress(int progress)
                {
                    if (message.state == IMMessageState.UploadItem)
                        notifyCallbackProgress(message, messageItem, progress, callback);
                }

                @Override
                public void onSuccess()
                {
                    if (message.state == IMMessageState.UploadItem)
                    {
                        holder.getMessageHandler().updateMessageItem(message);
                        sendRunnable.run();
                    }
                }

                @Override
                public void onError(String desc)
                {
                    if (message.state == IMMessageState.UploadItem)
                    {
                        message.state = IMMessageState.SendFail;
                        holder.getMessageHandler().updateMessageState(message);
                        notifyCallbackError(message, IMCode.ERROR_UPLOAD_ITEM, desc, callback);
                    }
                }
            });
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
    public void loadMessageBefore(int count, IMMessage lastMessage, final IMValueCallback<List<IMMessage>> callback)
    {
        if (!IMManager.getInstance().isLogin())
        {
            if (callback != null)
                callback.onError(IMCode.ERROR_NOT_LOGIN, "not login");
            return;
        }

        final IMConversationHandlerWrapper handler = IMManager.getInstance().getHandlerHolder().getConversationHandler();
        handler.loadMessageBefore(this, count, lastMessage, new IMValueCallback<List<IMMessage>>()
        {
            @Override
            public void onSuccess(List<IMMessage> list)
            {
                if (callback != null)
                    callback.onSuccess(list);
            }

            @Override
            public void onError(int code, String desc)
            {
                if (callback != null)
                    callback.onError(code, desc);
            }
        });
    }

    @Override
    public int hashCode()
    {
        return IMUtils.hash(peer, type);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (obj == null)
            return false;

        if (obj.getClass() != getClass())
            return false;

        final IMConversation other = (IMConversation) obj;
        return IMUtils.equals(peer, other.getPeer()) && IMUtils.equals(type, other.getType());
    }

    Accessor accessor()
    {
        return new Accessor();
    }

    public static Accessor newAccessor()
    {
        final IMConversation conversation = new IMConversation();
        return conversation.accessor();
    }

    public final class Accessor
    {
        private Accessor()
        {
        }

        public IMConversation getConversation()
        {
            return IMConversation.this;
        }

        public void setPeer(String peer)
        {
            IMConversation.this.peer = peer;
        }

        public void setType(IMConversationType type)
        {
            IMConversation.this.type = type;
        }

        public void setLastMessage(IMMessage message)
        {
            IMConversation.this.lastMessage = message;
        }

        public void setUnreadCount(int count)
        {
            IMConversation.this.unreadCount = count;
        }

        public void setLastTimestamp(long timestamp)
        {
            IMConversation.this.lastTimestamp = timestamp;
        }
    }

    public static void sort(List<IMConversation> list)
    {
        if (list == null || list.isEmpty())
            return;

        Collections.sort(list, new Comparator<IMConversation>()
        {
            @Override
            public int compare(IMConversation o1, IMConversation o2)
            {
                final long delta = o1.lastTimestamp - o2.lastTimestamp;
                if (delta > 0)
                {
                    return -1;
                } else if (delta < 0)
                {
                    return 1;
                } else
                {
                    return 0;
                }
            }
        });
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

                final Collection<IMOutgoingCallback> listCallback = IMManager.getInstance().getListIMOutgoingCallback();
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

                final Collection<IMOutgoingCallback> listCallback = IMManager.getInstance().getListIMOutgoingCallback();
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

                final Collection<IMOutgoingCallback> listCallback = IMManager.getInstance().getListIMOutgoingCallback();
                for (IMOutgoingCallback item : listCallback)
                {
                    item.onSuccess(message);
                }
            }
        });
    }
}
