package com.sd.lib.imsdk.handler.impl;

import com.google.gson.Gson;
import com.sd.lib.imsdk.IMManager;
import com.sd.lib.imsdk.IMMessageItem;
import com.sd.lib.imsdk.handler.IMMessageItemSerializer;

public class IMMessageItemSerializerGson implements IMMessageItemSerializer
{
    private final Gson mGson = new Gson();

    @Override
    public String serialize(Object object)
    {
        return mGson.toJson(object);
    }

    @Override
    public IMMessageItem deserialize(String itemContent, String itemType)
    {
        final Class<? extends IMMessageItem> clazz = IMManager.getInstance().getMessageItem(itemType);
        if (clazz == null)
            return null;

        return mGson.fromJson(itemContent, clazz);
    }
}
