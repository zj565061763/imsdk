package com.sd.lib.imsdk;

import com.sd.lib.imsdk.callback.IMCallback;
import com.sd.lib.imsdk.handler.IMSender;

public class IMConversation
{
    String peer;
    IMConversationType type;
    int unreadCount;

    public String getPeer()
    {
        return peer;
    }

    public IMConversationType getType()
    {
        return type;
    }

    public int getUnreadCount()
    {
        return unreadCount;
    }

    public IMMessage send(IMMessageItem item, IMCallback<IMMessage> callback)
    {
        if (item == null)
            throw new NullPointerException("item is null");

        final IMHandlerHolder holder = IMManager.getInstance().getHandlerHolder();
        final IMMessage message = IMFactory.newMessageSend(item);

        final IMSender.SendMessageRequest request = new IMSender.SendMessageRequest(peer, type, message, message.persistenceAccessor());
        holder.getSender().sendMessage(request, callback);
        return message;
    }
}
