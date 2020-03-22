package com.sd.lib.imsdk.callback;

public interface IMCallback<T>
{
    void onSuccess(T value);

    void onError(int code, String desc);
}
