package com.sd.lib.imsdk;

import com.sd.lib.imsdk.annotation.AIMMessageItem;
import com.sd.lib.imsdk.handler.IMMessageItemSerializer;

@AIMMessageItem(type = IMMessageItem.TYPE_TEXT)
public class IMMessageItemText extends IMMessageItem
{
    private String text;

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    @Override
    public String serialize(SerializeCallback callback)
    {
        final IMMessageItemSerializer serializer = IMManager.getInstance().getHandlerHolder().getMessageItemSerializer();
        final String content = serializer.serialize(this);
        return content;
    }
}
