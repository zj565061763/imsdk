package com.sd.lib.imsdk.callback;

public interface IMCallback
{
    void onSuccess();

    void onError(int code, String desc);
}
