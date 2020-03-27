package com.sd.lib.imsdk.model;

import android.text.TextUtils;

public class IMConversationExt
{
    private final String id;
    private String avatar;
    private String name;
    private String extra;

    public IMConversationExt(String id)
    {
        if (TextUtils.isEmpty(id))
            throw new IllegalArgumentException("id is empty");
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getExtra()
    {
        return extra;
    }

    public void setExtra(String extra)
    {
        this.extra = extra;
    }
}
