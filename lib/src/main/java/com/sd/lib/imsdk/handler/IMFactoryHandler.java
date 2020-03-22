package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMMessage;
import com.sd.lib.imsdk.model.IMUser;

public interface IMFactoryHandler
{
    void interceptNewMessageSend(IMMessage.InterceptAccessor accessor);

    IMUser getUser(String userId);
}
