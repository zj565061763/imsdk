package com.sd.lib.imsdk;

public enum IMConversationType
{
    single,
    group;

    public static IMConversationType from(String name)
    {
        try
        {
            return valueOf(name);
        } catch (Exception e)
        {
            return null;
        }
    }
}
