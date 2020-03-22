package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMUser;

public interface IMUserProvider
{
    IMUser getUser(String userId);

    IMUser getLoginUser();
}
