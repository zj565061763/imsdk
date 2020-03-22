package com.sd.lib.imsdk;

import com.sd.lib.imsdk.annotation.AIMMessageItem;

public abstract class IMMessageItem
{
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_FILE = "file";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_VIDEO = "video";

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

    /**
     * 序列化
     *
     * @param callback null-同步返回，不为null-异步返回
     * @return 序列化的字符串
     */
    public abstract String serialize(SerializeCallback callback);

    public interface SerializeCallback
    {
        void onProgress(int progress);

        void onSuccess(String serializeContent);

        void onError(String desc);
    }
}
