package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMMessageItem;

public interface IMMessageItemSerializer
{
    String serialize(IMMessageItem item);

    IMMessageItem deserialize(String content, Class<? extends IMMessageItem> clazz);
}
