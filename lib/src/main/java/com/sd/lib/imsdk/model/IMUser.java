package com.sd.lib.imsdk.model;

import android.text.TextUtils;

public class IMUser
{
    private final String id;
    private IMExt ext;

    public IMUser(String id)
    {
        if (TextUtils.isEmpty(id))
            throw new IllegalArgumentException("id is empty");
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public IMExt getExt()
    {
        if (ext == null)
            ext = new IMExt();
        return ext;
    }

    public void setExt(IMExt ext)
    {
        this.ext = ext;
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
