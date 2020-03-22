package com.sd.lib.imsdk;

import com.sd.lib.imsdk.model.IMUser;

public class IMMessage
{
    String id;
    long timestamp;
    IMMessageState state;
    IMUser sender;

    IMMessageItem item;

    IMMessage()
    {
    }

    public String getId()
    {
        return id;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public IMMessageState getState()
    {
        return state;
    }

    public IMUser getSender()
    {
        return sender;
    }

    public IMMessageItem getItem()
    {
        return item;
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
