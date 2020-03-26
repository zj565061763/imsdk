package com.sd.lib.imsdk;

import android.os.Handler;
import android.os.Looper;

class IMUtils
{
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(Runnable runnable)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            runnable.run();
        } else
        {
            HANDLER.post(runnable);
        }
    }

    public static boolean equals(Object a, Object b)
    {
        return a != null && a.equals(b);
    }
}
