package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.model.IMExt;

public interface IMPersistenceHandler
{
    void updateMessagePeerExt(String peer, IMConversationType conversationType, IMExt ext) throws Exception;
}
