package com.sd.lib.imsdk.callback;

import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.IMMessageItem;

public interface IMSendCallback
{
    void onProgress(IMMessage message, IMMessageItem item, int progress);

    void onSuccess(IMMessage message);

    void onError(IMMessage message, int code, String desc);
}
