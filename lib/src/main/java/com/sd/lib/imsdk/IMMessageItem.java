package com.sd.lib.imsdk;

import com.sd.lib.imsdk.annotation.AIMMessageItem;

public abstract class IMMessageItem
{
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_FILE = "file";
    public static final String TYPE_IMAGE = "image";

    private final String type;
    IMMessage message;

    public IMMessageItem()
    {
        final AIMMessageItem annotation = getClass().getAnnotation(AIMMessageItem.class);
        if (annotation == null)
            throw new RuntimeException(AIMMessageItem.class + " annotation was not found in class " + getClass());

        this.type = annotation.type();
    }

    public final String getType()
    {
        return type;
    }

    public final IMMessage getMessage()
    {
        return message;
    }

    public String toJson()
    {
        return null;
    }
}
