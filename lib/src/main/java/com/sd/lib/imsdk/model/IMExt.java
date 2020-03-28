package com.sd.lib.imsdk.model;

import com.sd.lib.imsdk.IMManager;

public class IMExt
{
    private String extId;
    private String extName;
    private String extAvatar;
    private String extra;

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
}
