package com.sd.lib.imsdk;

import com.sd.lib.imsdk.annotation.AIMMessageItem;

import java.util.Map;

@AIMMessageItem(type = "empty")
final class IMMessageItemEmpty extends IMMessageItem
{
    IMMessageItemEmpty()
    {
    }

    @Override
    protected void serializeItem(Map<String, Object> map, SerializeType type)
    {
    }
}
