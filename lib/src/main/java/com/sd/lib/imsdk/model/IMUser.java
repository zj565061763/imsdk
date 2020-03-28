package com.sd.lib.imsdk.model;

import android.text.TextUtils;

public class IMUser extends IMUserExt
{
    private String id;

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
