package com.sd.lib.imsdk.callback;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMMessage;

public interface OutgoingMessageCallback
{
    void onSend(IMMessage message, IMConversation conversation);

    void onSuccess(IMMessage message, IMConversation conversation);

    void onError(IMMessage message, IMConversation conversation);
}
