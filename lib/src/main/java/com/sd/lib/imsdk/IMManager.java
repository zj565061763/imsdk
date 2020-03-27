package com.sd.lib.imsdk;

import android.text.TextUtils;

import com.sd.lib.imsdk.annotation.AIMMessageItem;
import com.sd.lib.imsdk.callback.IMIncomingCallback;
import com.sd.lib.imsdk.callback.IMLoginStateCallback;
import com.sd.lib.imsdk.callback.IMOutgoingCallback;
import com.sd.lib.imsdk.exception.IMSDKException;
import com.sd.lib.imsdk.model.IMUser;
import com.sd.lib.imsdk.model.ReceiveMessage;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

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

    private final Collection<IMIncomingCallback> mListIMIncomingCallback = new CopyOnWriteArraySet<>();
    private final Collection<IMOutgoingCallback> mListIMOutgoingCallback = new CopyOnWriteArraySet<>();
    private final Collection<IMLoginStateCallback> mListIMLoginStateCallback = new CopyOnWriteArraySet<>();

    private volatile IMUser mLoginUser;
    private volatile IMConversation mChattingConversation;

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
    public synchronized void setLoginUser(IMUser user)
    {
        final IMUser old = mLoginUser;
        if (old == null && user == null)
            return;

        if (user != null && user.equals(old))
            return;

        mLoginUser = user;
        mMapConversation.clear();

        if (old != null)
        {
            for (IMLoginStateCallback item : mListIMLoginStateCallback)
            {
                item.onLogout(old.getId());
            }
        }

        if (user != null)
        {
            checkInterruptedMessage();
            for (IMLoginStateCallback item : mListIMLoginStateCallback)
            {
                item.onLogin(user.getId());
            }
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
        if (callback != null)
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
        if (callback != null)
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

    Collection<IMOutgoingCallback> getListIMOutgoingCallback()
    {
        return mListIMOutgoingCallback;
    }

    /**
     * 添加IM登陆状态回调
     *
     * @param callback
     */
    public synchronized void addIMLoginStateCallback(IMLoginStateCallback callback)
    {
        if (callback != null)
            mListIMLoginStateCallback.add(callback);
    }

    /**
     * 移除IM登陆状态回调
     *
     * @param callback
     */
    public synchronized void removeIMLoginStateCallback(IMLoginStateCallback callback)
    {
        mListIMLoginStateCallback.remove(callback);
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
            conversation.load();
            mMapConversation.put(key, conversation);
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
        final List<IMConversation> list = mHandlerHolder.getConversationHandler().getAllConversation();
        if (list == null || list.isEmpty())
            return null;

        for (IMConversation item : list)
        {
            final String key = item.getPeer() + "#" + item.getType();
            final IMConversation cache = mMapConversation.get(key);
            if (cache != null)
            {
                cache.read(item);
            } else
            {
                mMapConversation.put(key, item);
            }
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

        if (conversation != null)
            mHandlerHolder.getConversationHandler().removeConversation(conversation);
    }

    /**
     * 检查是否有在发送中被中断的消息
     */
    private void checkInterruptedMessage()
    {
        mHandlerHolder.getMessageHandler().checkInterruptedMessage();
    }

    /**
     * 处理消息接收
     *
     * @param receiveMessage {@link ReceiveMessage}
     * @return
     */
    public synchronized boolean handleReceiveMessage(ReceiveMessage receiveMessage) throws IMSDKException
    {
        if (!isLogin())
            throw new IMSDKException.UnLoginException("imsdk unlogin");

        receiveMessage.check();

        final Class<? extends IMMessageItem> clazz = IMManager.getInstance().getMessageItem(receiveMessage.itemType);
        if (clazz == null)
            throw new IMSDKException.MessageItemClassNotFoundException("message item class for type:" + receiveMessage.itemType + " was not found");

        IMMessageItem item = null;
        try
        {
            item = getHandlerHolder().getMessageItemSerializer().deserialize(receiveMessage.itemType, receiveMessage.itemContent);
        } catch (Exception e)
        {
            throw new IMSDKException.DeserializeMessageItemException("deserialize message item error for type:" + receiveMessage.itemType, e);
        }

        if (item == null)
            throw new IMSDKException.DeserializeMessageItemException("deserialize message item return null for type:" + receiveMessage.itemType);

        final IMMessage message = IMFactory.newMessageReceive();
        message.id = receiveMessage.id;
        message.timestamp = receiveMessage.timestamp;
        message.sender = receiveMessage.sender;
        message.peer = receiveMessage.sender.getId();
        message.conversationType = receiveMessage.conversationType;
        message.state = IMMessageState.Receive;
        message.isSelf = false;
        message.item = item;
        item.message = message;

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
