package com.sd.lib.imsdk.handler.impl;

import com.google.gson.Gson;
import com.sd.lib.imsdk.IMMessageItem;
import com.sd.lib.imsdk.handler.IMMessageItemSerializer;

public class IMMessageItemSerializerGson implements IMMessageItemSerializer
{
    private final Gson mGson = new Gson();

    @Override
    public String serialize(IMMessageItem item)
    {
        return mGson.toJson(item);
    }

    @Override
    public <T extends IMMessageItem> T deserialize(String content, Class<T> clazz)
    {
        return mGson.fromJson(content, clazz);
    }
}
