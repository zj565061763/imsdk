package com.sd.lib.imsdk;

import android.text.TextUtils;

import com.sd.lib.imsdk.annotation.AIMMessageItem;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
}
