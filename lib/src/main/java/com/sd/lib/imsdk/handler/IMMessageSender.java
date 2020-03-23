package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.callback.IMCallback;

public interface IMMessageSender
{
    void sendMessage(SendMessageRequest request, IMCallback<IMMessage> callback);

    class SendMessageRequest
    {
        public final IMConversationType conversationType;
        public final IMMessage message;
        public final IMMessage.PersistenceAccessor messageAccessor;

        public SendMessageRequest(IMConversationType conversationType, IMMessage message, IMMessage.PersistenceAccessor messageAccessor)
        {
            if (conversationType == null)
                throw new NullPointerException("conversationType is null");
            if (message == null)
                throw new NullPointerException("message is null");
            if (messageAccessor == null)
                throw new NullPointerException("messageAccessor is null");

            this.conversationType = conversationType;
            this.message = message;
            this.messageAccessor = messageAccessor;
        }
    }
}
