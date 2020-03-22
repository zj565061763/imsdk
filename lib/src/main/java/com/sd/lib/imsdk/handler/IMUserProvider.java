package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.model.IMUser;

public interface IMUserProvider
{
    IMUser getUser(String userId);
}
