package com.sd.lib.imsdk.callback;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMMessage;

public interface IncomingMessageCallback
{
    void onReceiveMessage(IMMessage message, IMConversation conversation);
}
