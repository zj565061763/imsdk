package com.sd.lib.imsdk;

import android.text.TextUtils;

import com.sd.lib.imsdk.annotation.AIMMessageItem;
import com.sd.lib.imsdk.callback.IMConversationChangeCallback;
import com.sd.lib.imsdk.callback.IMIncomingCallback;
import com.sd.lib.imsdk.callback.IMLoginStateCallback;
import com.sd.lib.imsdk.callback.IMOtherExceptionCallback;
import com.sd.lib.imsdk.callback.IMOutgoingCallback;
import com.sd.lib.imsdk.exception.IMSDKException;
import com.sd.lib.imsdk.model.IMConversationExt;
import com.sd.lib.imsdk.model.IMUser;
import com.sd.lib.imsdk.model.ReceiveMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
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

    private final Map<String, IMConversation> mMapConversationLocal = new LinkedHashMap<>();

    private final Collection<IMIncomingCallback> mListIMIncomingCallback = new CopyOnWriteArraySet<>();
    private final Collection<IMOutgoingCallback> mListIMOutgoingCallback = new CopyOnWriteArraySet<>();
    private final Collection<IMLoginStateCallback> mListIMLoginStateCallback = new CopyOnWriteArraySet<>();
    private final Collection<IMOtherExceptionCallback> mListIMOtherExceptionCallback = new CopyOnWriteArraySet<>();
    private final Collection<IMConversationChangeCallback> mListIMConversationChangeCallback = new CopyOnWriteArraySet<>();

    private volatile IMUser mLoginUser;
    private volatile IMConversation mChattingConversation;

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
    public void addIMIncomingCallback(IMIncomingCallback callback)
    {
        if (callback != null)
            mListIMIncomingCallback.add(callback);
    }

    /**
     * 移除新消息回调
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
     * 移除消息发送回调
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
     * 移除IM登陆状态回调
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
     * 移除其他异常回调
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
     * 移除会话变更回调
     *
     * @param callback
     */
    public void removeIMConversationChangeCallback(IMConversationChangeCallback callback)
    {
        mListIMConversationChangeCallback.remove(callback);
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
        if (list != null)
        {
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

                mMapConversationLocal.put(key, item);
            }
        }

        IMUtils.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                for (IMConversationChangeCallback item : mListIMConversationChangeCallback)
                {
                    item.onConversationLoad(list);
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
            removeConversationLocal(conversation);
    }

    synchronized void saveConversationLocal(final IMConversation conversation)
    {
        conversation.save();
        final int oldSize = mMapConversationLocal.size();

        final String key = conversation.getPeer() + "#" + conversation.getType();
        mMapConversationLocal.put(key, conversation);

        final int newSize = mMapConversationLocal.size();
        if (newSize != oldSize)
        {
            // 通知会话新增
            final List<IMConversation> list = new ArrayList<>(mMapConversationLocal.values());
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
            final List<IMConversation> list = new ArrayList<>(mMapConversationLocal.values());
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
     * 检查是否有在发送中被中断的消息
     */
    private void checkInterruptedMessage()
    {
        mHandlerHolder.getMessageHandler().checkInterruptedMessage();
    }

    /**
     * 处理消息接收
     *
     * @param receiveMessage
     * @throws IMSDKException
     */
    public void handleReceiveMessage(ReceiveMessage receiveMessage) throws IMSDKException
    {
        final IMMessage imMessage = handleReceiveMessageInternal(receiveMessage);
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
     * @return
     */
    private synchronized IMMessage handleReceiveMessageInternal(ReceiveMessage receiveMessage) throws IMSDKException
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

        final IMMessage imMessage = IMFactory.newMessageReceive();
        imMessage.id = receiveMessage.id;
        imMessage.timestamp = receiveMessage.timestamp;
        imMessage.peer = receiveMessage.peer;
        imMessage.conversationType = receiveMessage.conversationType;
        imMessage.state = IMMessageState.Receive;
        imMessage.isSelf = false;
        imMessage.item = item;
        imMessage.sender = receiveMessage.sender.copy();
        item.message = imMessage;

        if (mChattingConversation != null)
            imMessage.isRead = imMessage.peer.equals(mChattingConversation.getPeer());
        else
            imMessage.isRead = false;

        getHandlerHolder().getMessageHandler().saveMessage(imMessage);

        final IMConversation conversation = getConversation(imMessage.getPeer(), imMessage.getConversationType());
        conversation.lastTimestamp = System.currentTimeMillis();
        conversation.lastMessage = imMessage;

        final IMConversationExt conversationExt = receiveMessage.conversationExt;
        if (conversation != null)
            conversation.getExt().read(conversationExt);
        saveConversationLocal(conversation);

        return imMessage;
    }
}
