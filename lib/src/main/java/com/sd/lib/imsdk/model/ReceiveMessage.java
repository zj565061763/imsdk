package com.sd.lib.imsdk.model;

import android.text.TextUtils;

import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.exception.IMSDKException;

public class ReceiveMessage
{
    public final String id;
    public final long timestamp;
    public final String itemType;
    public final String itemContent;
    public final String peer;
    public final IMConversationType conversationType;
    public final IMUser sender;

    private ReceiveMessage(Builder builder)
    {
        this.id = builder.id;
        this.timestamp = builder.timestamp;
        this.itemType = builder.itemType;
        this.itemContent = builder.itemContent;
        this.peer = builder.peer;
        this.conversationType = builder.conversationType;

        final IMUser sender = builder.sender;
        final IMUser imUser = new IMUser(sender.getId());
        imUser.setAvatar(sender.getAvatar());
        imUser.setName(sender.getName());
        imUser.setExtra(sender.getExtra());
        this.sender = imUser;
    }

    public void check() throws IMSDKException
    {
        if (TextUtils.isEmpty(id))
            throw new IMSDKException.IllegalReceiveMessageException("error id is empty");

        if (timestamp <= 0)
            throw new IMSDKException.IllegalReceiveMessageException("error id is empty");

        if (TextUtils.isEmpty(itemType))
            throw new IMSDKException.IllegalReceiveMessageException("error itemType is empty");

        if (TextUtils.isEmpty(itemContent))
            throw new IMSDKException.IllegalReceiveMessageException("error itemContent is empty");

        if (TextUtils.isEmpty(peer))
            throw new IMSDKException.IllegalReceiveMessageException("error peer is empty");

        if (conversationType == null)
            throw new IMSDKException.IllegalReceiveMessageException("error conversationType is null");

        if (sender == null)
            throw new IMSDKException.IllegalReceiveMessageException("error sender is null");
    }

    public static class Builder
    {
        private String id;
        private long timestamp;
        private String itemType;
        private String itemContent;
        private String peer;
        private IMConversationType conversationType;
        private IMUser sender;

        public Builder setId(String id)
        {
            this.id = id;
            return this;
        }

        public Builder setTimestamp(long timestamp)
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setItemType(String itemType)
        {
            this.itemType = itemType;
            return this;
        }

        public Builder setItemContent(String itemContent)
        {
            this.itemContent = itemContent;
            return this;
        }

        public Builder setPeer(String peer)
        {
            this.peer = peer;
            return this;
        }

        public Builder setConversationType(IMConversationType conversationType)
        {
            this.conversationType = conversationType;
            return this;
        }

        public Builder setSender(IMUser sender)
        {
            this.sender = sender;
            return this;
        }

        public ReceiveMessage build()
        {
            return new ReceiveMessage(this);
        }
    }
}
