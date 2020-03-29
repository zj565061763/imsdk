package com.sd.lib.imsdk.callback;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMMessage;

/**
 * 聊天中的会话事件回调
 */
public interface IMChattingConversationEventCallback
{
    void onSenderExtChanged(IMConversation conversation, IMMessage imMessage);
}
