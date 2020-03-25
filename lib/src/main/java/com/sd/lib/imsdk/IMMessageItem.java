package com.sd.lib.imsdk;

import com.sd.lib.imsdk.annotation.AIMMessageItem;

public abstract class IMMessageItem
{
    private final String type;
    transient IMMessage message;
    transient int uploadProgress;

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

    public String serialize()
    {
        final String content = IMManager.getInstance().getHandlerHolder().getMessageItemSerializer().serialize(this);
        return content;
    }

    /**
     * 上传
     *
     * @param callback
     * @return
     */
    boolean upload(final UploadCallback callback)
    {
        return uploadImpl(new UploadCallback()
        {
            @Override
            public void onProgress(final int progress)
            {
                IMMessageItem.this.uploadProgress = progress;
                if (callback != null)
                    callback.onProgress(progress);
            }

            @Override
            public void onSuccess()
            {
                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(final String desc)
            {
                if (callback != null)
                    callback.onError(desc);
            }
        });
    }

    protected boolean uploadImpl(UploadCallback callback)
    {
        return false;
    }

    protected boolean isNeedUpload()
    {
        return false;
    }

    public interface UploadCallback
    {
        void onProgress(int progress);

        void onSuccess();

        void onError(String desc);
    }
}
