package com.sd.lib.imsdk;

import android.text.TextUtils;

import com.sd.lib.imsdk.annotation.AIMMessageItem;
import com.sd.lib.imsdk.callback.IMIncomingCallback;
import com.sd.lib.imsdk.callback.IMOutgoingCallback;
import com.sd.lib.imsdk.model.IMUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class IMManager
{
    private static IMManager sInstance;

    private IMManager()
    {
    }

    public static IMManager getInstance()
    {
        if (sInstance == null)
        {
            synchronized (IMManager.class)
            {
                if (sInstance == null)
                    sInstance = new IMManager();
            }
        }
        return sInstance;
    }

    private final IMHandlerHolder mHandlerHolder = new IMHandlerHolder();
    private final Map<String, IMConversation> mMapConversation = new ConcurrentHashMap<>();
    private final Map<String, Class<? extends IMMessageItem>> mMapMessageItemClass = new HashMap<>();

    private final List<IMIncomingCallback> mListIMIncomingCallback = new CopyOnWriteArrayList<>();
    private final List<IMOutgoingCallback> mListIMOutgoingCallback = new CopyOnWriteArrayList<>();

    private IMUser mLoginUser;
    private IMConversation mChattingConversation;

    public IMHandlerHolder getHandlerHolder()
    {
        return mHandlerHolder;
    }

    /**
     * 注册{@link IMMessageItem}
     *
     * @param clazz
     */
    public synchronized void registerMessageItem(Class<? extends IMMessageItem> clazz)
    {
        if (clazz == null)
            return;

        final AIMMessageItem annotation = clazz.getAnnotation(AIMMessageItem.class);
        if (annotation == null)
            throw new IllegalArgumentException(AIMMessageItem.class + " annotation was not found in class " + clazz);

        final String type = annotation.type();
        mMapMessageItemClass.put(type, clazz);
    }

    /**
     * 返回type对应的{@link IMMessageItem}
     *
     * @param type
     * @return
     */
    public synchronized Class<? extends IMMessageItem> getMessageItem(String type)
    {
        if (TextUtils.isEmpty(type))
            return null;
        return mMapMessageItemClass.get(type);
    }

    /**
     * 是否已登录
     *
     * @return
     */
    public boolean isLogin()
    {
        return mLoginUser != null;
    }

    /**
     * 返回登录的用户
     *
     * @return
     */
    public IMUser getLoginUser()
    {
        return mLoginUser;
    }

    /**
     * 设置登录用户
     *
     * @param user
     */
    public void setLoginUser(IMUser user)
    {
        mLoginUser = user;

        if (user != null)
        {
            if (TextUtils.isEmpty(user.getId()))
            {
                mLoginUser = null;
                throw new IllegalArgumentException("user id is empty");
            }
        } else
        {
            mMapConversation.clear();
        }
    }

    /**
     * 返回当前正在聊天的会话
     *
     * @return
     */
    public IMConversation getChattingConversation()
    {
        return mChattingConversation;
    }

    /**
     * 设置当前正在聊天的会话
     *
     * @param conversation
     */
    public synchronized void setChattingConversation(IMConversation conversation)
    {
        if (conversation == null)
            throw new NullPointerException("conversation is null");
        mChattingConversation = conversation;
    }

    /**
     * 移除当前正在聊天的会话标识
     *
     * @param conversation
     */
    public synchronized void removeChattingConversation(IMConversation conversation)
    {
        if (conversation == null)
            return;

        if (conversation.equals(mChattingConversation))
            mChattingConversation = null;
    }

    /**
     * 添加新消息回调
     *
     * @param callback
     */
    public synchronized void addIMIncomingCallback(IMIncomingCallback callback)
    {
        mListIMIncomingCallback.add(callback);
    }

    /**
     * 移除新消息回调
     *
     * @param callback
     */
    public synchronized void removeIMIncomingCallback(IMIncomingCallback callback)
    {
        mListIMIncomingCallback.remove(callback);
    }

    /**
     * 添加消息发送回调
     *
     * @param callback
     */
    public synchronized void addIMOutgoingCallback(IMOutgoingCallback callback)
    {
        mListIMOutgoingCallback.add(callback);
    }

    /**
     * 移除消息发送回调
     *
     * @param callback
     */
    public synchronized void removeIMOutgoingCallback(IMOutgoingCallback callback)
    {
        mListIMOutgoingCallback.remove(callback);
    }

    List<IMOutgoingCallback> getListIMOutgoingCallback()
    {
        return mListIMOutgoingCallback;
    }

    /**
     * 返回某个会话
     *
     * @param peer
     * @param type
     * @return
     */
    public synchronized IMConversation getConversation(String peer, IMConversationType type)
    {
        if (TextUtils.isEmpty(peer) || type == null)
            return null;

        final String key = peer + "#" + type;
        IMConversation conversation = mMapConversation.get(key);
        if (conversation == null)
        {
            conversation = IMFactory.newConversation(peer, type);
            mMapConversation.put(key, conversation);
            conversation.load();
        }
        return conversation;
    }

    /**
     * 返回所有会话
     *
     * @return
     */
    public synchronized List<IMConversation> getAllConversation()
    {
        final List<IMConversation> list = new ArrayList<>(mMapConversation.size());
        for (IMConversation item : mMapConversation.values())
        {
            if (item.load())
                list.add(item);
        }

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
        return list;
    }

    /**
     * 移除会话
     *
     * @param peer
     * @param type
     */
    public synchronized void removeConversation(String peer, IMConversationType type)
    {
        if (TextUtils.isEmpty(peer) || type == null)
            return;

        final String key = peer + "#" + type;
        final IMConversation conversation = mMapConversation.remove(key);
        if (conversation == null)
            return;

        mHandlerHolder.getConversationHandler().removeConversation(conversation);
    }

    /**
     * 处理消息接收
     *
     * @param messageId        消息ID
     * @param timestamp        消息时间戳
     * @param itemType         消息类型
     * @param itemContent      消息内容
     * @param conversationType 会话类型
     * @param sender           消息发送者
     * @return
     */
    public synchronized boolean handleReceiveMessage(String messageId, long timestamp,
                                                     String itemType, String itemContent,
                                                     IMConversationType conversationType, IMUser sender)
    {
        if (!isLogin())
            return false;

        if (TextUtils.isEmpty(itemType)
                || TextUtils.isEmpty(messageId)
                || timestamp <= 0
                || conversationType == null
                || sender == null
                || TextUtils.isEmpty(sender.getId()))
        {
            return false;
        }

        final IMMessageItem item = getHandlerHolder().getMessageItemSerializer().deserialize(itemContent, itemType);
        if (item == null)
            return false;

        final IMMessage message = IMFactory.newMessageReceive();
        message.id = messageId;
        message.timestamp = timestamp;
        message.sender = sender;
        message.peer = sender.getId();
        message.conversationType = conversationType;
        message.state = IMMessageState.Receive;
        message.isSelf = false;
        message.item = item;

        if (mChattingConversation != null)
            message.isRead = message.peer.equals(mChattingConversation.getPeer());
        else
            message.isRead = false;

        getHandlerHolder().getMessageHandler().saveMessage(message);

        final IMConversation conversation = getConversation(message.getPeer(), message.getConversationType());
        conversation.lastTimestamp = System.currentTimeMillis();
        conversation.lastMessage = message;
        getHandlerHolder().getConversationHandler().saveConversation(conversation);

        IMUtils.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                for (IMIncomingCallback callback : mListIMIncomingCallback)
                {
                    callback.onReceiveMessage(message);
                }
            }
        });

        return true;
    }
}
