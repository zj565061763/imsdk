package com.sd.lib.imsdk.exception;

public class IMSDKException extends Exception
{
    public IMSDKException(String message)
    {
        super(message);
    }

    public IMSDKException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public static class UnLoginException extends IMSDKException
    {
        public UnLoginException(String message)
        {
            super(message);
        }

        public UnLoginException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }

    public static class IllegalReceiveMessageException extends IMSDKException
    {
        public IllegalReceiveMessageException(String message)
        {
            super(message);
        }

        public IllegalReceiveMessageException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }

    public static class DeserializeMessageItemException extends IMSDKException
    {
        public DeserializeMessageItemException(String message)
        {
            super(message);
        }

        public DeserializeMessageItemException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }

    public static class MessageItemClassNotFoundException extends IMSDKException
    {
        public MessageItemClassNotFoundException(String message)
        {
            super(message);
        }

        public MessageItemClassNotFoundException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }

    public static class OtherException extends IMSDKException
    {
        public OtherException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }
}
