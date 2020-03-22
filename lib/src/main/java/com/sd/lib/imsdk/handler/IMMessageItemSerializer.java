package com.sd.lib.imsdk.handler;

import com.sd.lib.imsdk.IMMessageItem;

public interface IMMessageItemSerializer
{
    String serialize(IMMessageItem item);

    <T extends IMMessageItem> T deserialize(String content, Class<T> clazz);
}
