package com.sd.lib.imsdk.handler.impl;

import com.sd.lib.imsdk.IMConversationType;
import com.sd.lib.imsdk.IMHandlerHolder;
import com.sd.lib.imsdk.handler.IMPersistenceHandler;
import com.sd.lib.imsdk.model.IMExt;

public class IMPersistenceHandlerWrapper implements IMPersistenceHandler
{
    private final IMPersistenceHandler mOriginal;
    private final IMHandlerHolder.CallbackHandler mCallbackHandler;

    public IMPersistenceHandlerWrapper(IMPersistenceHandler original, IMHandlerHolder.CallbackHandler callbackHandler)
    {
        if (original == null)
            original = new IMPersistenceHandlerEmpty();

        mOriginal = original;
        mCallbackHandler = callbackHandler;
    }

    @Override
    public void updateMessagePeerExt(String peer, IMConversationType conversationType, IMExt ext)
    {
        try
        {
            mOriginal.updateMessagePeerExt(peer, conversationType, ext);
        } catch (Exception e)
        {
            mCallbackHandler.notifyOtherException("error updateMessagePeerExt peer:" + peer + " type:" + conversationType, e);
        }
    }
}
