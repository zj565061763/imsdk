package com.sd.lib.imsdk.callback;

import com.sd.lib.imsdk.IMConversation;

import java.util.List;

public interface IMConversationChangeCallback
{
    void onConversationLoad(List<IMConversation> list);

    void onConversationAdd(List<IMConversation> list, IMConversation conversation);

    void onConversationRemove(List<IMConversation> list, IMConversation conversation);
}
