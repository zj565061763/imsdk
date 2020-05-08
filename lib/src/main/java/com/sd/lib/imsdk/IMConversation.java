package com.sd.lib.imsdk;

import com.sd.lib.imsdk.callback.IMCallback;
import com.sd.lib.imsdk.callback.IMOutgoingCallback;
import com.sd.lib.imsdk.callback.IMSendCallback;
import com.sd.lib.imsdk.callback.IMValueCallback;
import com.sd.lib.imsdk.constant.IMCode;
import com.sd.lib.imsdk.handler.impl.IMConversationHandlerWrapper;
import com.sd.lib.imsdk.model.IMConversationExt;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IMConversation
{
    private String peer;
    private IMConversationType type;

    private volatile IMMessage lastMessage;
    private volatile int unreadCount;
    private volatile long lastTimestamp;

    private IMConversationExt ext;
    private final Config config = new Config();
    private IMMessage firstMessage;

    IMConversation()
    {
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

    public Config getConfig()
    {
        return config;
    }

    public IMMessage getFirstMessage()
    {
        if (firstMessage == null)
        {
            if (IMManager.getInstance().isLogin())
            {
                IMManager.getInstance().getHandlerHolder().getConversationHandler().loadFirstMessage(this, new IMValueCallback<IMMessage>()
                {
                    @Override
                    public void onSuccess(IMMessage value)
                    {
                        firstMessage = value;
                    }

                    @Override
                    public void onError(int code, String desc)
                    {
                    }
                });
            }
        }
        return firstMessage;
    }

    synchronized void read(IMConversation conversation)
    {
        if (!peer.equals(conversation.peer))
            throw new IllegalArgumentException("read conversation error peer");

        if (!type.equals(conversation.type))
            throw new IllegalArgumentException("read conversation error type");

        setLastMessage(conversation.getLastMessage());
        setUnreadCount(conversation.getUnreadCount(), false);
        getExt().read(conversation.getExt());
    }

    /**
     * 加载会话
     */
    public void load()
    {
        if (!IMManager.getInstance().isLogin())
            return;

        removeUnreadCountRunnable();

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
        setUnreadCount(unreadCount, true);
        return getUnreadCount();
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
        setUnreadCount(0, true);
    }

    /**
     * 保存扩展内容到本地
     */
    public void updateExt()
    {
        if (!IMManager.getInstance().isLogin())
            return;

        final IMConversationHandlerWrapper handler = IMManager.getInstance().getHandlerHolder().getConversationHandler();
        handler.updateConversationExt(this);
    }

    /**
     * 保存会话
     */
    void save()
    {
        if (!IMManager.getInstance().isLogin())
            return;

        if (getConfig().saveLocal)
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

        final IMMessage message = IMFactory.newMessageSend();
        message.setPeer(peer);
        message.setConversationType(type);
        message.setSelf(true);
        message.setRead(true);
        message.setItem(item);
        message.setSender(IMManager.getInstance().getLoginUser());

        return sendInternal(message, callback);
    }

    private IMMessage sendInternal(final IMMessage message, final IMSendCallback callback)
    {
        if (!IMManager.getInstance().isLogin())
        {
            if (callback != null)
                callback.onError(null, IMCode.ERROR_NOT_LOGIN, "not login");
            return message;
        }

        if (message.getItem().isEmpty())
        {
            if (callback != null)
                callback.onError(null, IMCode.ERROR_EMPTY_ITEM, "empty message item");
            return null;
        }

        final IMHandlerHolder holder = IMManager.getInstance().getHandlerHolder();

        message.setState(IMMessageState.send_prepare);
        message.save();

        setLastMessage(message);
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
                message.setState(IMMessageState.sending);
                holder.getMessageHandler().updateMessageState(message);

                holder.getMessageSender().sendMessage(message, new IMCallback()
                {
                    @Override
                    public void onSuccess()
                    {
                        message.setState(IMMessageState.send_success);
                        holder.getMessageHandler().updateMessageState(message);
                        notifyCallbackSuccess(message, callback);
                    }

                    @Override
                    public void onError(int code, String desc)
                    {
                        message.setState(IMMessageState.send_fail);
                        holder.getMessageHandler().updateMessageState(message);
                        notifyCallbackError(message, code, desc, callback);
                    }
                });
            }
        };

        final IMMessageItem messageItem = message.getItem();
        if (messageItem.isNeedUpload())
        {
            message.setState(IMMessageState.upload_item);
            holder.getMessageHandler().updateMessageState(message);

            messageItem.upload(new IMMessageItem.UploadCallback()
            {
                @Override
                public void onProgress(int progress)
                {
                    if (message.getState() == IMMessageState.upload_item)
                        notifyCallbackProgress(message, messageItem, progress, callback);
                }

                @Override
                public void onSuccess()
                {
                    if (message.getState() == IMMessageState.upload_item)
                    {
                        message.updateItem();
                        sendRunnable.run();
                    }
                }

                @Override
                public void onError(String desc)
                {
                    if (message.getState() == IMMessageState.upload_item)
                    {
                        message.setState(IMMessageState.send_fail);
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

    //---------- setter ----------

    void setPeer(String peer)
    {
        this.peer = peer;
    }

    void setType(IMConversationType type)
    {
        this.type = type;
    }

    synchronized void setLastMessage(IMMessage lastMessage)
    {
        final IMMessage old = this.lastMessage;
        if (old != null && lastMessage != null)
        {
            if (lastMessage.getTimestamp() < old.getTimestamp())
                return;
        }

        this.lastMessage = lastMessage;

        if (lastMessage != null)
            setLastTimestamp(lastMessage.getTimestamp());
    }

    void setUnreadCount(int unreadCount, boolean notifyUnreadCount)
    {
        removeUnreadCountRunnable();

        boolean changed = false;
        synchronized (IMConversation.this)
        {
            final int old = this.unreadCount;
            if (old != unreadCount)
            {
                this.unreadCount = unreadCount;
                changed = true;
            }
        }

        if (notifyUnreadCount && changed)
            IMManager.getInstance().updateUnreadCount();
    }

    private synchronized void setLastTimestamp(long lastTimestamp)
    {
        if (lastTimestamp > this.lastTimestamp)
            this.lastTimestamp = lastTimestamp;
    }

    private void setFirstMessage(IMMessage message)
    {
        this.firstMessage = message;
    }

    @Override
    public int hashCode()
    {
        return IMUtils.hash(peer, type);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;

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
            IMConversation.this.setPeer(peer);
        }

        public void setType(IMConversationType type)
        {
            IMConversation.this.setType(type);
        }

        public void setLastMessage(IMMessage message)
        {
            IMConversation.this.setLastMessage(message);
        }

        public void setUnreadCount(int count)
        {
            IMConversation.this.setUnreadCount(count, false);
        }

        public void setLastTimestamp(long timestamp)
        {
            IMConversation.this.setLastTimestamp(timestamp);
        }

        public void setFirstMessage(IMMessage message)
        {
            IMConversation.this.setFirstMessage(message);
        }
    }

    public static final class Config
    {
        public volatile boolean saveLocal = true;
        public volatile boolean saveMessage = true;
        public volatile boolean receiveMessageTips = true;
    }

    void postUnreadCountRunnable()
    {
        removeUnreadCountRunnable();
        IMUtils.HANDLER.postDelayed(mLoadUnreadCountRunnable, 1000);
    }

    private void removeUnreadCountRunnable()
    {
        IMUtils.HANDLER.removeCallbacks(mLoadUnreadCountRunnable);
    }

    private final Runnable mLoadUnreadCountRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            loadUnreadCount();
        }
    };

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
