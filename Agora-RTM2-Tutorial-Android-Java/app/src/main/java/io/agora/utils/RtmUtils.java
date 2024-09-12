package io.agora.utils;

public class RtmUtils {
    public enum StorageOperation {
        SUBSCRIBE_CHANNEL_METADATA,
        UNSUBSCRIBE_CHANNEL_METADATA,
        SET_CHANNEL_METADATA,
        UPDATE_CHANNEL_METADATA,
        REMOVE_CHANNEL_METADATA,
        GET_CHANNEL_METADATA,
        SET_USER_METADATA,
        UPDATE_USER_METADATA,
        REMOVE_USER_METADATA,
        GET_USER_METADATA,
        SUBSCRIBE_USER_METADATA,
        UNSUBSCRIBE_USER_METADATA,
    }

    public enum LockOperation {
        SUBSCRIBE_LOCK,
        REMOVE_LOCK,
        SET_LOCK,
        ACQUIRE_LOCK,
        GET_LOCKS,
        RELEASE_LOCK,
        UNSUBSCRIBE_LOCK,
    }

    public enum PresenceOperation {
        SUBSCRIBE_PRESENCE,
        WHO_NOW,
        WHERE_NOW,
        SET_STATE,
        REMOVE_STATE,
        GET_STATE,
        UNSUBSCRIBE_PRESENCE,
    }

    public enum LogLevel {
        INFO,
        ERROR,
        CALLBACK,
        API,
    }
}
