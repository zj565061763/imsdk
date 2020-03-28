package com.sd.lib.imsdk.handler.impl;

import com.google.gson.Gson;
import com.sd.lib.imsdk.handler.IMJsonSerializer;

public class IMJsonSerializerGson implements IMJsonSerializer
{
    private final Gson mGson = new Gson();

    @Override
    public String serialize(Object src)
    {
        return mGson.toJson(src);
    }

    @Override
    public <T> T deserialize(String json, Class<T> classOfT)
    {
        return mGson.fromJson(json, classOfT);
    }
}
