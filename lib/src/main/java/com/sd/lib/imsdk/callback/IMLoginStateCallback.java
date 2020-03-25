package com.sd.lib.imsdk.callback;

public interface IMLoginStateCallback
{
    void onLogin(String userId);

    void onLogout(String userId);
}
