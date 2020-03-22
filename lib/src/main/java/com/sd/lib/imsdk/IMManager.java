package com.sd.lib.imsdk;

import android.text.TextUtils;

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

    public IMHandlerHolder getHandlerHolder()
    {
        return mHandlerHolder;
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
