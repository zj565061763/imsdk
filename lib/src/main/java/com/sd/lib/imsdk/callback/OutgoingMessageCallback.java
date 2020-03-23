package com.sd.lib.imsdk.callback;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMMessage;

public interface OutgoingMessageCallback
{
    void onSend(IMMessage message);

    void onSuccess(IMMessage message);

    void onError(IMMessage message);
}
