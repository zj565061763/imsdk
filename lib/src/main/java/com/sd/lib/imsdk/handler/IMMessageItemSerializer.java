package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMMessageItem;

public interface IMMessageItemSerializer
{
    String serialize(IMMessageItem item);

    IMMessageItem deserialize(String content, String itemType, Class<? extends IMMessageItem> clazz);
}
