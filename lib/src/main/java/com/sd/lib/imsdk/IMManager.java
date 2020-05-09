package com.sd.lib.imsdk;

import android.text.TextUtils;

import com.sd.lib.imsdk.annotation.AIMMessageItem;
import com.sd.lib.imsdk.callback.IMChattingConversationEventCallback;
import com.sd.lib.imsdk.callback.IMConversationChangeCallback;
import com.sd.lib.imsdk.callback.IMHandleReceiveMessageCallback;
import com.sd.lib.imsdk.callback.IMIncomingCallback;
import com.sd.lib.imsdk.callback.IMLoginStateCallback;
import com.sd.lib.imsdk.callback.IMOtherExceptionCallback;
import com.sd.lib.imsdk.callback.IMOutgoingCallback;
import com.sd.lib.imsdk.callback.IMUnreadCountChangeCallback;
import com.sd.lib.imsdk.exception.IMSDKException;
import com.sd.lib.imsdk.model.IMConversationExt;
import com.sd.lib.imsdk.model.IMUser;
import com.sd.lib.imsdk.model.ReceiveMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    private final Map<String, Class<? extends IMMessageItem>> mMapMessageItemClass = new ConcurrentHashMap<>();

    private final Map<String, IMConversation> mMapConversation = new ConcurrentHashMap<>();
    private final Map<String, IMConversation> mMapConversationLocal = new ConcurrentHashMap<>();

    private final Collection<IMIncomingCallback> mListIMIncomingCallback = new CopyOnWriteArraySet<>();
    private final Collection<IMOutgoingCallback> mListIMOutgoingCallback = new CopyOnWriteArraySet<>();
    private final Collection<IMLoginStateCallback> mListIMLoginStateCallback = new CopyOnWriteArraySet<>();
    private final Collection<IMOtherExceptionCallback> mListIMOtherExceptionCallback = new CopyOnWriteArraySet<>();
    private final Collection<IMConversationChangeCallback> mListIMConversationChangeCallback = new CopyOnWriteArraySet<>();
    private final Collection<IMUnreadCountChangeCallback> mListIMUnreadCountChangeCallback = new CopyOnWriteArraySet<>();

    private volatile IMUser mLoginUser;
    private volatile int mUnreadCount;

    private final Map<IMConversation, String> mMapChattingConversation = new ConcurrentHashMap<>();
    private final Map<IMUser, IMMessage> mMapChattingMessageLatest = new ConcurrentHashMap<>();
    private final Collection<IMChattingConversationEventCallback> mListIMChattingConversationEventCallback = new CopyOnWriteArraySet<>();

    public IMHandlerHolder getHandlerHolder()
    {
        return mHandlerHolder;
    }

    private final IMHandlerHolder mHandlerHolder = new IMHandlerHolder(new IMHandlerHolder.CallbackHandler()
    {
        @Override
        public void notifyOtherException(String message, Exception e)
        {
            final IMSDKException.OtherException exception = new IMSDKException.OtherException(message, e);
            IMUtils.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    for (IMOtherExceptionCallback item : mListIMOtherExceptionCallback)
                    {
                        item.handleOtherException(exception);
                    }
                }
            });
        }
    });

    /**
     * 注册{@link IMMessageItem}
     *
     * @param clazz
     */
    public void registerMessageItem(Class<? extends IMMessageItem> clazz)
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
    public Class<? extends IMMessageItem> getMessageItem(String type)
    {
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
    public synchronized void setLoginUser(final IMUser user)
    {
        final IMUser old = mLoginUser;
        if (old == null && user == null)
            return;

        if (user != null && user.equals(old))
            return;

        setUnreadCount(0);

        mLoginUser = user;

        mMapConversation.clear();
        mMapConversationLocal.clear();

        if (old != null)
        {
            IMUtils.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    for (IMLoginStateCallback item : mListIMLoginStateCallback)
                    {
                        item.onLogout(old.getId());
                    }
                }
            });
        }

        if (user != null)
        {
            checkInterruptedMessage();
            IMUtils.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    for (IMLoginStateCallback item : mListIMLoginStateCallback)
                    {
                        item.onLogin(user.getId());
                    }
                }
            });
        }
    }

    /**
     * 添加当前正在聊天的会话
     *
     * @param conversation
     */
    public synchronized void addChattingConversation(IMConversation conversation)
    {
        if (conversation == null)
            throw new NullPointerException("conversation is null");

        mMapChattingConversation.put(conversation, "");
    }

    /**
     * 移除当前正在聊天的会话标识
     *
     * @param conversation
     */
    public synchronized void removeChattingConversation(IMConversation conversation)
    {
        mMapChattingConversation.remove(conversation);
        if (mMapChattingConversation.isEmpty())
            mMapChattingMessageLatest.clear();
    }

    /**
     * 添加新消息回调
     *
     * @param callback
     */
    public void addIMIncomingCallback(IMIncomingCallback callback)
    {
        if (callback != null)
            mListIMIncomingCallback.add(callback);
    }

    /**
     * 移除回调
     *
     * @param callback
     */
    public void removeIMIncomingCallback(IMIncomingCallback callback)
    {
        mListIMIncomingCallback.remove(callback);
    }

    /**
     * 添加消息发送回调
     *
     * @param callback
     */
    public void addIMOutgoingCallback(IMOutgoingCallback callback)
    {
        if (callback != null)
            mListIMOutgoingCallback.add(callback);
    }

    /**
     * 移除回调
     *
     * @param callback
     */
    public void removeIMOutgoingCallback(IMOutgoingCallback callback)
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
    public void addIMLoginStateCallback(IMLoginStateCallback callback)
    {
        if (callback != null)
            mListIMLoginStateCallback.add(callback);
    }

    /**
     * 移除回调
     *
     * @param callback
     */
    public void removeIMLoginStateCallback(IMLoginStateCallback callback)
    {
        mListIMLoginStateCallback.remove(callback);
    }

    /**
     * 添加其他异常回调
     *
     * @param callback
     */
    public void addIMOtherExceptionCallback(IMOtherExceptionCallback callback)
    {
        if (callback != null)
            mListIMOtherExceptionCallback.add(callback);
    }

    /**
     * 移除回调
     *
     * @param callback
     */
    public void removeIMOtherExceptionCallback(IMOtherExceptionCallback callback)
    {
        mListIMOtherExceptionCallback.remove(callback);
    }

    /**
     * 添加会话变更回调
     *
     * @param callback
     */
    public void addIMConversationChangeCallback(IMConversationChangeCallback callback)
    {
        if (callback != null)
            mListIMConversationChangeCallback.add(callback);
    }

    /**
     * 移除回调
     *
     * @param callback
     */
    public void removeIMConversationChangeCallback(IMConversationChangeCallback callback)
    {
        mListIMConversationChangeCallback.remove(callback);
    }

    /**
     * 添加会话中发送者扩展信息变更回调
     *
     * @param callback
     */
    public void addIMChattingConversationEventCallback(IMChattingConversationEventCallback callback)
    {
        if (callback != null)
            mListIMChattingConversationEventCallback.add(callback);
    }

    /**
     * 移除回调
     *
     * @param callback
     */
    public void removeIMChattingConversationEventCallback(IMChattingConversationEventCallback callback)
    {
        mListIMChattingConversationEventCallback.remove(callback);
    }

    /**
     * 添加未读数量变化通知
     *
     * @param callback
     */
    public void addIMUnreadCountChangeCallback(IMUnreadCountChangeCallback callback)
    {
        if (callback != null)
            mListIMUnreadCountChangeCallback.add(callback);
    }

    /**
     * 移除回调
     *
     * @param callback
     */
    public void removeIMUnreadCountChangeCallback(IMUnreadCountChangeCallback callback)
    {
        mListIMUnreadCountChangeCallback.remove(callback);
    }

    void notifyIMUnreadCountChangeCallback(final int count)
    {
        if (!isLogin())
            return;

        IMUtils.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                for (IMUnreadCountChangeCallback item : mListIMUnreadCountChangeCallback)
                {
                    item.onUnreadCountChanged(count);
                }
            }
        });
    }

    /**
     * 返回某个会话
     *
     * @param peer
     * @param type
     * @return
     */
    public IMConversation getConversation(String peer, IMConversationType type)
    {
        return getConversationInternal(peer, type);
    }

    private synchronized IMConversation getConversationInternal(String peer, IMConversationType type)
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
     * 返回所有已经缓存在内存中的会话
     *
     * @return
     */
    public List<IMConversation> getAllConversation()
    {
        if (!isLogin())
            return Collections.emptyList();

        return new ArrayList<>(mMapConversationLocal.values());
    }

    /**
     * 加载所有会话
     *
     * @return
     */
    public synchronized List<IMConversation> loadAllConversation()
    {
        if (!isLogin())
            return Collections.emptyList();

        final List<IMConversation> list = mHandlerHolder.getConversationHandler().getAllConversation();

        final Map<String, IMConversation> mapConversationLocal = new HashMap<>();
        int unreadCount = 0;

        if (list != null)
        {
            for (IMConversation item : list)
            {
                final IMConversation cache = getConversationInternal(item.getPeer(), item.getType());
                cache.read(item);

                if (cache.getLastTimestamp() > 0)
                {
                    final String key = item.getPeer() + "#" + item.getType();
                    mapConversationLocal.put(key, cache);
                }

                unreadCount += item.getUnreadCount();
            }
        }

        mMapConversationLocal.clear();
        mMapConversationLocal.putAll(mapConversationLocal);
        setUnreadCount(unreadCount);

        final List<IMConversation> listResult = getAllConversation();

        IMUtils.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                for (IMConversationChangeCallback item : mListIMConversationChangeCallback)
                {
                    item.onConversationLoad(listResult);
                }
            }
        });

        return listResult;
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
        {
            conversation.setUnreadCount(0, true);
            removeConversationLocal(conversation);
        }
    }

    synchronized void saveConversationLocal(final IMConversation conversation)
    {
        if (!conversation.getConfig().saveLocal)
            return;

        conversation.save();
        final int oldSize = mMapConversationLocal.size();

        final String key = conversation.getPeer() + "#" + conversation.getType();
        mMapConversationLocal.put(key, conversation);

        final int newSize = mMapConversationLocal.size();
        if (newSize != oldSize)
        {
            // 通知会话新增
            final List<IMConversation> list = getAllConversation();
            IMUtils.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    for (IMConversationChangeCallback item : mListIMConversationChangeCallback)
                    {
                        item.onConversationAdd(list, conversation);
                    }
                }
            });
        }
    }

    synchronized void removeConversationLocal(final IMConversation conversation)
    {
        getHandlerHolder().getConversationHandler().removeConversation(conversation);
        final int oldSize = mMapConversationLocal.size();

        final String key = conversation.getPeer() + "#" + conversation.getType();
        mMapConversationLocal.remove(key);

        final int newSize = mMapConversationLocal.size();
        if (newSize != oldSize)
        {
            // 通知会话移除
            final List<IMConversation> list = getAllConversation();
            IMUtils.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    for (IMConversationChangeCallback item : mListIMConversationChangeCallback)
                    {
                        item.onConversationRemove(list, conversation);
                    }
                }
            });
        }
    }

    /**
     * 获得未读数量
     *
     * @return
     */
    public int getUnreadCount()
    {
        return mUnreadCount;
    }

    synchronized void updateUnreadCount()
    {
        int count = 0;
        for (IMConversation item : mMapConversationLocal.values())
        {
            count += item.getUnreadCount();
        }
        setUnreadCount(count);
    }

    private synchronized void setUnreadCount(int unreadCount)
    {
        if (unreadCount < 0)
            unreadCount = 0;

        if (mUnreadCount != unreadCount)
        {
            mUnreadCount = unreadCount;
            notifyIMUnreadCountChangeCallback(mUnreadCount);
        }
    }

    /**
     * 检查是否有在发送中被中断的消息
     */
    private void checkInterruptedMessage()
    {
        mHandlerHolder.getMessageHandler().checkInterruptedMessage();
    }

    /**
     * 处理正在聊天中的会话消息
     *
     * @param imMessage
     */
    public synchronized void processChattingConversationMessage(IMMessage imMessage)
    {
        if (imMessage.isSelf())
            return;

        final IMConversation conversation = imMessage.getConversation();
        if (!mMapChattingConversation.containsKey(conversation))
            return;

        final IMUser sender = imMessage.getSender();
        final IMMessage cache = mMapChattingMessageLatest.get(sender);
        if (cache == null)
        {
            // 如果缓存不存在，则当前消息就是最新的消息
            mMapChattingMessageLatest.put(sender, imMessage);
            return;
        }

        if (imMessage.getTimestamp() < cache.getTimestamp())
        {
            // 如果当前消息比较旧，则更新发送者信息
            imMessage.getSender().read(cache.getSender());
        } else
        {
            // 更新的消息
            mMapChattingMessageLatest.put(sender, imMessage);
            final IMUser cacheSender = cache.getSender();
            if (cacheSender.isExtChanged(sender))
            {
                // sender变化
                notifyChattingSenderChanged(imMessage);
            }
        }
    }

    private void notifyChattingSenderChanged(final IMMessage imMessage)
    {
        final IMConversation conversation = imMessage.getConversation();
        IMUtils.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                for (IMChattingConversationEventCallback item : mListIMChattingConversationEventCallback)
                {
                    item.onSenderExtChanged(conversation, imMessage);
                }
            }
        });
    }

    /**
     * 处理消息接收
     *
     * @param receiveMessage
     * @throws IMSDKException
     */
    public void handleReceiveMessage(ReceiveMessage receiveMessage, IMHandleReceiveMessageCallback callback) throws IMSDKException
    {
        final IMMessage imMessage = handleReceiveMessageInternal(receiveMessage, callback);

        IMUtils.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                for (IMIncomingCallback callback : mListIMIncomingCallback)
                {
                    callback.onReceiveMessage(imMessage);
                }
            }
        });
    }

    /**
     * 处理消息接收
     *
     * @param receiveMessage {@link ReceiveMessage}
     * @param callback
     * @return
     */
    private synchronized IMMessage handleReceiveMessageInternal(ReceiveMessage receiveMessage, IMHandleReceiveMessageCallback callback) throws IMSDKException
    {
        final IMUser loginUser = mLoginUser;
        if (loginUser == null)
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

        final boolean isSelf = TextUtils.equals(loginUser.getId(), receiveMessage.sender.getId());

        final IMMessage imMessage = IMFactory.newMessageReceive();
        imMessage.setId(receiveMessage.id);
        imMessage.setTimestamp(receiveMessage.timestamp);
        imMessage.setPeer(receiveMessage.peer);
        imMessage.setConversationType(receiveMessage.conversationType);
        imMessage.setItem(item);
        imMessage.setSelf(isSelf);

        if (isSelf)
        {
            imMessage.setState(IMMessageState.send_success);
            imMessage.setSender(loginUser.copy());
            imMessage.setRead(true);
        } else
        {
            imMessage.setState(IMMessageState.receive);
            imMessage.setSender(receiveMessage.sender.copy());
        }

        final IMConversation conversation = imMessage.getConversation();
        if (mMapChattingConversation.containsKey(conversation))
            imMessage.setRead(true);

        if (callback != null)
            callback.onCreate(imMessage, imMessage.accessor());

        imMessage.save();

        conversation.setLastMessage(imMessage);
        final IMConversationExt conversationExt = receiveMessage.conversationExt;
        if (conversationExt != null)
            conversation.getExt().read(conversationExt);

        saveConversationLocal(conversation);
        processChattingConversationMessage(imMessage);

        if (conversation.getConfig().saveMessage)
        {
            if (!imMessage.isRead() && !isSelf)
                conversation.postUnreadCountRunnable();
        }

        return imMessage;
    }
}
