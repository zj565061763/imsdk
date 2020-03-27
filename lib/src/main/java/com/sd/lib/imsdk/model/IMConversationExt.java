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

    public IMConversationExt copy()
    {
        final IMConversationExt copy = new IMConversationExt(this.id);
        copy.avatar = this.avatar;
        copy.name = this.name;
        copy.extra = this.extra;
        return copy;
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

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (obj == null)
            return false;

        if (obj.getClass() != getClass())
            return false;

        final IMConversationExt other = (IMConversationExt) obj;
        return id.equals(other.getId());
    }
}
