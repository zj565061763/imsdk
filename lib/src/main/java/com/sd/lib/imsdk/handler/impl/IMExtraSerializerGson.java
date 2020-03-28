package com.sd.lib.imsdk.handler.impl;

import com.google.gson.Gson;
import com.sd.lib.imsdk.handler.IMExtraSerializer;

public class IMExtraSerializerGson implements IMExtraSerializer
{
    private final Gson mGson = new Gson();

    @Override
    public <T> T fromJson(String json, Class<T> classOfT)
    {
        return mGson.fromJson(json, classOfT);
    }
}
