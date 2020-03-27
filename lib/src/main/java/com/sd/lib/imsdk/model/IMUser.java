package com.sd.lib.imsdk.model;

import android.text.TextUtils;

public class IMUser
{
    private final String id;
    private String bsId;
    private String bsName;
    private String bsAvatar;
    private String extra;

    public IMUser(String id)
    {
        if (TextUtils.isEmpty(id))
            throw new IllegalArgumentException("im user id is null");
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public String getBsId()
    {
        return bsId;
    }

    public void setBsId(String bsId)
    {
        this.bsId = bsId;
    }

    public String getBsName()
    {
        return bsName;
    }

    public void setBsName(String bsName)
    {
        this.bsName = bsName;
    }

    public String getBsAvatar()
    {
        return bsAvatar;
    }

    public void setBsAvatar(String bsAvatar)
    {
        this.bsAvatar = bsAvatar;
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

        final IMUser other = (IMUser) obj;
        return id.equals(other.getId());
    }
}
