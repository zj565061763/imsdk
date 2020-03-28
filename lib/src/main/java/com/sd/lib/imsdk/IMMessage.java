package com.sd.lib.imsdk;

import com.sd.lib.imsdk.callback.IMSendCallback;
import com.sd.lib.imsdk.model.IMConversationExt;
import com.sd.lib.imsdk.model.IMUser;

public class IMMessage
{
    String id;
    long timestamp;

    String peer;
    IMConversationType conversationType;
    IMMessageState state = IMMessageState.None;
    boolean isSelf;
    boolean isRead;

    IMUser sender;
    IMMessageItem item;
    IMConversationExt conversationExt;

    IMMessage()
    {
    }

    /**
     * 重新发送失败的消息
     *
     * @param callback
     * @return
     */
    public boolean resend(IMSendCallback callback)
    {
        if (isSelf && state == IMMessageState.SendFail)
        {
            IMManager.getInstance().getHandlerHolder().getMessageHandler().removeMessage(this);
            IMManager.getInstance().getConversation(peer, conversationType).send(getItem(), callback);
            return true;
        }
        return false;
    }

    /**
     * 保存当前消息
     */
    public void save()
    {
        IMManager.getInstance().getHandlerHolder().getMessageHandler().saveMessage(this);
    }

    public String getId()
    {
        return id;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public String getPeer()
    {
        return peer;
    }

    public IMConversationType getConversationType()
    {
        return conversationType;
    }

    public IMMessageState getState()
    {
        return state;
    }

    public boolean isSelf()
    {
        return isSelf;
    }

    public boolean isRead()
    {
        return isRead;
    }

    public IMUser getSender()
    {
        return sender;
    }

    public IMMessageItem getItem()
    {
        if (item == null)
        {
            item = new IMMessageItemEmpty();
            item.message = this;
        }
        return item;
    }

    public IMConversationExt getConversationExt()
    {
        return conversationExt;
    }

    public void setConversationExt(IMConversationExt conversationExt)
    {
        this.conversationExt = conversationExt;
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

        final IMMessage other = (IMMessage) obj;
        return IMUtils.equals(id, other.getId());
    }

    Accessor accessor()
    {
        return new Accessor();
    }

    public static Accessor newAccessor()
    {
        final IMMessage message = new IMMessage();
        return message.accessor();
    }


    public final class Accessor
    {
        private Accessor()
        {
        }

        public IMMessage getMessage()
        {
            return IMMessage.this;
        }

        public void setId(String id)
        {
            IMMessage.this.id = id;
        }

        public void setTimestamp(long timestamp)
        {
            IMMessage.this.timestamp = timestamp;
        }

        public void setSender(IMUser sender)
        {
            IMMessage.this.sender = sender;
        }

        public void setPeer(String peer)
        {
            IMMessage.this.peer = peer;
        }

        public void setConversationType(IMConversationType conversationType)
        {
            IMMessage.this.conversationType = conversationType;
        }

        public void setState(IMMessageState state)
        {
            IMMessage.this.state = state;
        }

        public void setSelf(boolean self)
        {
            IMMessage.this.isSelf = self;
        }

        public void setRead(boolean read)
        {
            IMMessage.this.isRead = read;
        }

        public void setItem(IMMessageItem item)
        {
            IMMessage.this.item = item;
            item.message = IMMessage.this;
        }
    }
}
