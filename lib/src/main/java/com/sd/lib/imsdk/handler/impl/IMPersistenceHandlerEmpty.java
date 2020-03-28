package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.handler.IMPersistenceHandler;
import com.sd.lib.imsdk.model.IMExt;

class IMPersistenceHandlerEmpty implements IMPersistenceHandler
{
    @Override
    public void updateMessagePeerExt(String peer, IMConversationType conversationType, IMExt ext) throws Exception
    {

    }
}
