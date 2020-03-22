package com.sd.lib.imsdk;

import com.sd.lib.imsdk.handler.IMFactoryHandler;
import com.sd.lib.imsdk.handler.IMUserProvider;

import java.util.UUID;

class IMFactory
{
    public static IMMessage newMessageSend(IMMessageItem item)
    {
        final IMUserProvider userProvider = IMManager.getInstance().getHandlerHolder().getUserProvider();

        final IMMessage message = new IMMessage();
        message.id = UUID.randomUUID().toString();
        message.timestamp = System.currentTimeMillis();
        message.item = item;
        message.user = userProvider.getLoginUser();
        item.message = message;

        final IMFactoryHandler factoryHandler = IMManager.getInstance().getHandlerHolder().getFactoryHandler();
        factoryHandler.interceptNewMessageSend(message.interceptAccessor());
        return message;
    }
}
