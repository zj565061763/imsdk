package com.sd.lib.imsdk;

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

    public IMHandlerHolder getHandlerHolder()
    {
        return mHandlerHolder;
    }

    public IMConversation getConversation(String peer, IMConversationType type)
    {
        final IMConversation conversation = new IMConversation();
        return conversation;
    }
}
