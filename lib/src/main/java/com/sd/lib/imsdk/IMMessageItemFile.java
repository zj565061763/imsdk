package com.sd.lib.imsdk;

import com.sd.lib.imsdk.annotation.AIMMessageItem;

@AIMMessageItem(type = IMMessageItem.TYPE_FILE)
public class IMMessageItemFile extends IMMessageItem
{
    private String url;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
