package com.sd.lib.imsdk;

import com.sd.lib.imsdk.handler.IMFactoryHandler;
import com.sd.lib.imsdk.handler.IMMessagePersistence;
import com.sd.lib.imsdk.handler.IMSender;
import com.sd.lib.imsdk.handler.IMUserProvider;

public class IMHandlerHolder
{
    private IMFactoryHandler mFactoryHandler;
    private IMSender mSender;
    private IMMessagePersistence mMessagePersistence;
    private IMUserProvider mUserProvider;

    public IMFactoryHandler getFactoryHandler()
    {
        return mFactoryHandler;
    }

    public void setFactoryHandler(IMFactoryHandler factoryHandler)
    {
        mFactoryHandler = factoryHandler;
    }

    public IMSender getSender()
    {
        return mSender;
    }

    public void setSender(IMSender sender)
    {
        mSender = sender;
    }

    public IMMessagePersistence getMessagePersistence()
    {
        return mMessagePersistence;
    }

    public void setMessagePersistence(IMMessagePersistence persistence)
    {
        mMessagePersistence = persistence;
    }

    public IMUserProvider getUserProvider()
    {
        return mUserProvider;
    }

    public void setUserProvider(IMUserProvider provider)
    {
        mUserProvider = provider;
    }
}
