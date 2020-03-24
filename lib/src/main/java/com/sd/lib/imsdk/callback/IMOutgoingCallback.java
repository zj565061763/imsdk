package com.sd.lib.imsdk.callback;

import com.sd.lib.imsdk.IMMessage;

public interface IMOutgoingCallback extends IMSendCallback
{
    void onSend(IMMessage message);
}
