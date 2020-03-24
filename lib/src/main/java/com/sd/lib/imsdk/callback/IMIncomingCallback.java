package com.sd.lib.imsdk.callback;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMMessage;

public interface IMIncomingCallback
{
    void onReceiveMessage(IMMessage message);
}
