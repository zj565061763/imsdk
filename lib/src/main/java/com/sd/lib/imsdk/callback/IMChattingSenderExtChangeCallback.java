package com.sd.lib.imsdk.callback;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.model.IMUser;

/**
 * 会话中发送者扩展信息变更回调
 */
public interface IMChattingSenderExtChangeCallback
{
    void onSenderExtChanged(IMConversation conversation, IMUser sender);
}
