package com.sd.lib.imsdk.model;

import android.text.TextUtils;

import com.sd.lib.imsdk.IMManager;

public class IMConversationExt
{
    private String id;
    private String name;
    private String avatar;
    private String extra;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        if (TextUtils.isEmpty(id))
            throw new IllegalArgumentException("id is empty");
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getExtra()
    {
        return extra;
    }

    public void setExtra(String extra)
    {
        this.extra = extra;
    }

    public <T> T parseExtra(Class<T> clazz)
    {
        if (clazz == null)
            return null;

        final T object = IMManager.getInstance().getHandlerHolder().getJsonSerializer().deserialize(extra, clazz);
        return object;
    }

    public String serialize()
    {
        return IMManager.getInstance().getHandlerHolder().getJsonSerializer().serialize(this);
    }

    public static IMConversationExt deserialize(String content)
    {
        return IMManager.getInstance().getHandlerHolder().getJsonSerializer().deserialize(content, IMConversationExt.class);
    }
}
