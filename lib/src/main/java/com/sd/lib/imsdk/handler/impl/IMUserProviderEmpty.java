package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.handler.IMUserProvider;
import com.sd.lib.imsdk.model.IMUser;

public class IMUserProviderEmpty implements IMUserProvider
{
    @Override
    public IMUser getUser(String userId)
    {
        return null;
    }
}
