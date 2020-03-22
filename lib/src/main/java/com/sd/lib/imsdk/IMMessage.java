package com.sd.lib.imsdk;

import com.sd.lib.imsdk.model.IMUser;

public class IMMessage
{
    IMMessageItem item;

    String id;
    long timestamp;
    boolean isRead;
    IMMessageState state;
    IMUser user;

    IMMessage()
    {
    }

    public IMMessageItem getItem()
    {
        return item;
    }

    public String getId()
    {
        return id;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public boolean isRead()
    {
        return isRead;
    }

    public IMMessageState getState()
    {
        return state;
    }

    public IMUser getUser()
    {
        return user;
    }

    PersistenceAccessor persistenceAccessor()
    {
        return new PersistenceAccessor();
    }

    InterceptAccessor interceptAccessor()
    {
        return new InterceptAccessor();
    }

    public final class InterceptAccessor
    {
    }

    public final class PersistenceAccessor
    {
        public void setState(IMMessageState state)
        {
            IMMessage.this.state = state;
        }
    }
}
