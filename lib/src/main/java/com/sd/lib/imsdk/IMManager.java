package com.sd.lib.imsdk;

import android.os.Looper;
import android.text.TextUtils;

import com.sd.lib.imsdk.annotation.AIMMessageItem;
import com.sd.lib.imsdk.callback.IncomingMessageCallback;
import com.sd.lib.imsdk.callback.OutgoingMessageCallback;
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

    private IMUser mLoginUser;

    private final IMHandlerHolder mHandlerHolder = new IMHandlerHolder();
    private final Map<String, IMConversation> mMapConversation = new ConcurrentHashMap<>();
    private final Map<String, Class<? extends IMMessageItem>> mMapMessageItemClass = new HashMap<>();

    private final List<IncomingMessageCallback> mListIncomingMessageCallback = new CopyOnWriteArrayList<>();
    private final List<OutgoingMessageCallback> mListOutgoingMessageCallback = new CopyOnWriteArrayList<>();

    public IMHandlerHolder getHandlerHolder()
    {
        return mHandlerHolder;
    }

    /**
     * 注册{@link IMMessageItem}
     *
     * @param clazz
     * @param <T>
     */
    public synchronized <T extends IMMessageItem> void registerMessageItem(Class<T> clazz)
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
    }

    /**
     * 添加新消息回调
     *
     * @param callback
     */
    public synchronized void addIncomingMessageCallback(IncomingMessageCallback callback)
    {
        mListIncomingMessageCallback.add(callback);
    }

    /**
     * 移除新消息回调
     *
     * @param callback
     */
    public synchronized void removeIncomingMessageCallback(IncomingMessageCallback callback)
    {
        mListIncomingMessageCallback.remove(callback);
    }

    /**
     * 添加消息发送回调
     *
     * @param callback
     */
    public synchronized void addOutgoingMessageCallback(OutgoingMessageCallback callback)
    {
        mListOutgoingMessageCallback.add(callback);
    }

    /**
     * 移除消息发送回调
     *
     * @param callback
     */
    public synchronized void removeOutgoingMessageCallback(OutgoingMessageCallback callback)
    {
        mListOutgoingMessageCallback.remove(callback);
    }

    List<OutgoingMessageCallback> getListOutgoingMessageCallback()
    {
        return mListOutgoingMessageCallback;
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
        final List<IMConversation> list = new ArrayList<>(mMapConversation.values());
        for (IMConversation item : list)
        {
            item.load();
        }

        Collections.sort(list, new Comparator<IMConversation>()
        {
            @Override
            public int compare(IMConversation o1, IMConversation o2)
            {
                return 0;
            }
        });
        return list;
    }

    /**
     * 处理消息接收
     */
    public synchronized boolean handleReceiveMessage(String type, String messageId, long timestamp,
                                                     IMConversationType conversationType, IMUser user, String content)
    {
        if (TextUtils.isEmpty(type)
                || TextUtils.isEmpty(messageId)
                || timestamp <= 0
                || conversationType == null
                || user == null
                || TextUtils.isEmpty(user.getId()))
        {
            return false;
        }

        final Class<? extends IMMessageItem> clazz = mMapMessageItemClass.get(type);
        if (clazz == null)
            return false;

        final IMMessageItem item = getHandlerHolder().getMessageItemSerializer().deserialize(content, clazz);
        if (item == null)
            return false;

        final IMMessage message = IMFactory.newMessageReceive();
        message.id = messageId;
        message.timestamp = timestamp;
        message.sender = user;
        message.peer = user.getId();
        message.conversationType = conversationType;
        message.isSelf = false;
        message.state = IMMessageState.Receive;
        message.item = item;

        getHandlerHolder().getConversationHandler().saveConversation(user.getId(), conversationType, message);
        getHandlerHolder().getMessageHandler().saveMessage(message, conversationType);

        IMUtils.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                for (IncomingMessageCallback callback : mListIncomingMessageCallback)
                {
                    callback.onReceiveMessage(message);
                }
            }
        });

        return true;
    }

    private static void runOnUiThread(Runnable runnable)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            runnable.run();
        } else
        {

        }
    }
}
