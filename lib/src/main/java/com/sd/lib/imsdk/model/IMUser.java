package com.sd.lib.imsdk.model;

import android.text.TextUtils;

public class IMUser
{
    private final String id;

    private String extId;
    private String extName;
    private String extAvatar;
    private String extExtra;

    public IMUser(String id)
    {
        if (TextUtils.isEmpty(id))
            throw new IllegalArgumentException("id is empty");
        this.id = id;
    }

    public IMUser copy()
    {
        final IMUser copy = new IMUser(this.id);
        copy.extId = this.extId;
        copy.extName = this.extName;
        copy.extAvatar = this.extAvatar;
        copy.extExtra = this.extExtra;
        return copy;
    }

    public String getId()
    {
        return id;
    }

    public String getExtId()
    {
        return extId;
    }

    public void setExtId(String extId)
    {
        this.extId = extId;
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

    public String getExtExtra()
    {
        return extExtra;
    }

    public void setExtExtra(String extExtra)
    {
        this.extExtra = extExtra;
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
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
        return id != null && id.equals(other.getId());
    }
}
