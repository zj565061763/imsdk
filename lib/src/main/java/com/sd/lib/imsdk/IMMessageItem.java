package com.sd.lib.imsdk;

import com.sd.lib.imsdk.annotation.AIMMessageItem;

import java.util.HashMap;
import java.util.Map;

public abstract class IMMessageItem
{
    private transient final String type;
    transient IMMessage message;
    private transient int uploadProgress;

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

    public final int getUploadProgress()
    {
        return uploadProgress;
    }

    public boolean isEmpty()
    {
        return getClass() == IMMessageItemEmpty.class;
    }

    /**
     * 返回用于会话列表展示的描述
     *
     * @return
     */
    public CharSequence getConversationDescription()
    {
        return "";
    }

    /**
     * 序列化
     *
     * @param type {@link SerializeType}
     * @return
     */
    public final String serialize(SerializeType type)
    {
        if (type == null)
            throw new NullPointerException("type is null");

        final Map<String, Object> map = new HashMap<>();
        serializeItem(map, type);

        final String content = IMManager.getInstance().getHandlerHolder().getMessageItemSerializer().serialize(map);
        return content;
    }

    /**
     * 序列化Item，保存信息到map中
     *
     * @param map
     * @param type {@link SerializeType}
     */
    protected abstract void serializeItem(Map<String, Object> map, SerializeType type);

    /**
     * 上传
     *
     * @param callback
     */
    void upload(final UploadCallback callback)
    {
        uploadImpl(new UploadCallback()
        {
            @Override
            public void onProgress(final int progress)
            {
                IMMessageItem.this.uploadProgress = progress;
                callback.onProgress(progress);
            }

            @Override
            public void onSuccess()
            {
                callback.onSuccess();
            }

            @Override
            public void onError(final String desc)
            {
                callback.onError(desc);
            }
        });
    }

    protected void uploadImpl(UploadCallback callback)
    {
    }

    protected boolean isNeedUpload()
    {
        return false;
    }

    protected void delete()
    {
    }

    public interface UploadCallback
    {
        void onProgress(int progress);

        void onSuccess();

        void onError(String desc);
    }

    public enum SerializeType
    {
        /**
         * 持久化到本地
         */
        persistence,
        /**
         * 发送消息
         */
        send
    }
}
