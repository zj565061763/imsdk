package com.sd.lib.imsdk;

import com.sd.lib.imsdk.model.IMUser;

public class IMMessage
{
    String id;
    long timestamp;
    IMMessageState state;
    IMUser sender;
    boolean isSelf;
    String peer;

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

    public boolean isSelf()
    {
        return isSelf;
    }

    public String getPeer()
    {
        return peer;
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
        private InterceptAccessor()
        {
        }
    }

    public final class PersistenceAccessor
    {
        private PersistenceAccessor()
        {
        }

        public void setState(IMMessageState state)
        {
            IMMessage.this.state = state;
        }
    }
}
