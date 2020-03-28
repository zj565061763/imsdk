package com.sd.lib.imsdk.handler;

public interface IMExtraSerializer
{
    <T> T fromJson(String json, Class<T> classOfT);
}
