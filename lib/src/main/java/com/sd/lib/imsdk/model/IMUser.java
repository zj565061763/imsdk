package com.sd.lib.imsdk.model;

import android.text.TextUtils;

public class IMUser
{
    private final String id;
    private final String extId;
    private String extName;
    private String extAvatar;
    private String extra;

    public IMUser(String id, String extId)
    {
        if (TextUtils.isEmpty(id))
            throw new IllegalArgumentException("id is empty");

        if (TextUtils.isEmpty(extId))
            throw new IllegalArgumentException("extId is empty");
        this.id = id;
        this.extId = extId;
    }

    public String getId()
    {
        return id;
    }

    public String getExtId()
    {
        return extId;
    }

    public String getExtName()
    {
        return extName;
    }

    public void setExtName(String extName)
    {
        this.extName = extName;
    }

    public String getExtAvatar()
    {
        return extAvatar;
    }

    public void setExtAvatar(String extAvatar)
    {
        this.extAvatar = extAvatar;
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
