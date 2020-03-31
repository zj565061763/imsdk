package com.example.imsdk;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.sd.lib.imsdk.IMConversation;
import com.sd.lib.imsdk.IMManager;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final List<IMConversation> list = IMManager.getInstance().loadAllConversation();
        Log.i(TAG, "size:" + list.size());
    }
}
