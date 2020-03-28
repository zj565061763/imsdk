package com.sd.lib.imsdk.model;

import android.text.TextUtils;

import com.sd.lib.imsdk.IMManager;

public class IMUser
{
    private String id;

    private String extId;
    private String extName;
    private String extAvatar;
    private String extra;

    public IMUser()
    {
    }

    public IMUser(String id)
    {
        setId(id);
    }

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

    public static IMUser deserialize(String content)
    {
        return IMManager.getInstance().getHandlerHolder().getJsonSerializer().deserialize(content, IMUser.class);
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
