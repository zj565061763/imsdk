package com.sd.lib.imsdk;

import com.sd.lib.imsdk.callback.IMSendCallback;
import com.sd.lib.imsdk.model.IMUser;

public class IMMessage
{
    private String id;
    private long timestamp;

    private String peer;
    private IMConversationType conversationType;
    private IMMessageState state = IMMessageState.none;
    private boolean isSelf;
    private boolean isRead;

    private IMMessageItem item;
    private IMUser sender;

    IMMessage()
    {
    }

    public IMConversation getConversation()
    {
        return IMManager.getInstance().getConversation(peer, conversationType);
    }

    /**
     * 重新发送失败的消息
     *
     * @param callback
     * @return
     */
    public IMMessage resend(IMSendCallback callback)
    {
        if (!isSelf)
            throw new RuntimeException("can not send other user message");

        if (state == IMMessageState.send_fail)
        {
            IMManager.getInstance().getHandlerHolder().getMessageHandler().deleteMessage(this);
            return getConversation().send(getItem(), callback);
        }
        return this;
    }

    /**
     * 把Item保存到本地
     */
    public void updateItem()
    {
        if (!IMManager.getInstance().isLogin())
            return;

        IMManager.getInstance().getHandlerHolder().getMessageHandler().updateMessageItem(this);
    }

    /**
     * 删除消息
     */
    public void delete()
    {
        if (item != null)
            item.delete();
        IMManager.getInstance().getHandlerHolder().getMessageHandler().deleteMessage(this);
    }

    /**
     * 保存当前消息
     */
    void save()
    {
        if (!IMManager.getInstance().isLogin())
            return;

        final IMConversation conversation = getConversation();
        if (conversation.getConfig().saveMessage)
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

    public IMMessageItem getItem()
    {
        if (item == null)
        {
            item = new IMMessageItemEmpty();
            item.message = this;
        }
        return item;
    }

    public IMUser getSender()
    {
        return sender;
    }

    //---------- setter ----------

    void setId(String id)
    {
        this.id = id;
    }

    void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    void setPeer(String peer)
    {
        this.peer = peer;
    }

    void setConversationType(IMConversationType conversationType)
    {
        this.conversationType = conversationType;
    }

    void setState(IMMessageState state)
    {
        this.state = state;
    }

    void setSelf(boolean self)
    {
        isSelf = self;
    }

    void setRead(boolean read)
    {
        isRead = read;
    }

    void setItem(IMMessageItem item)
    {
        this.item = item;
        item.message = IMMessage.this;
    }

    void setSender(IMUser sender)
    {
        this.sender = sender;
    }

    @Override
    public int hashCode()
    {
        return IMUtils.hash(id);
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
            IMMessage.this.setId(id);
        }

        public void setTimestamp(long timestamp)
        {
            IMMessage.this.setTimestamp(timestamp);
        }

        public void setPeer(String peer)
        {
            IMMessage.this.setPeer(peer);
        }

        public void setConversationType(IMConversationType conversationType)
        {
            IMMessage.this.setConversationType(conversationType);
        }

        public void setState(IMMessageState state)
        {
            IMMessage.this.setState(state);
        }

        public void setSelf(boolean self)
        {
            IMMessage.this.setSelf(self);
        }

        public void setRead(boolean read)
        {
            IMMessage.this.setRead(read);
        }

        public void setItem(IMMessageItem item)
        {
            IMMessage.this.setItem(item);
        }

        public void setSender(IMUser sender)
        {
            IMMessage.this.setSender(sender);
        }
    }
}
