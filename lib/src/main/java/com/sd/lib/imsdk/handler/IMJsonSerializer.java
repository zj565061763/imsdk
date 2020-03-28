package com.sd.lib.imsdk.handler;

public interface IMJsonSerializer
{
    String serialize(Object src);

    <T> T deserialize(String json, Class<T> classOfT);
}
