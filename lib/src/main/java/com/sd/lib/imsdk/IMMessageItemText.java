package com.sd.lib.imsdk;

import com.sd.lib.imsdk.annotation.AIMMessageItem;

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
    public String toJson()
    {
        return null;
    }
}
