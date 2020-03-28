package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.model.IMConversationExt;

public interface IMPersistenceHandler
{
    void updateConversationExt(String peer, IMConversationType conversationType, IMConversationExt ext) throws Exception;
}
