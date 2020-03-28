package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.handler.IMPersistenceHandler;
import com.sd.lib.imsdk.model.IMConversationExt;

class IMPersistenceHandlerEmpty implements IMPersistenceHandler
{
    @Override
    public void updateConversationExt(String peer, IMConversationType conversationType, IMConversationExt ext) throws Exception
    {

    }
}
