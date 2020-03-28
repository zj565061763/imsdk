package com.sd.lib.imsdk.handler;

public interface IMExtraSerializer
{
    <T> T deserialize(String json, Class<T> classOfT);
}
