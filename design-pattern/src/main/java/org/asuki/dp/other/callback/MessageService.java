package org.asuki.dp.other.callback;

public interface MessageService {
    void registerMessageCallback(MessageCallback callback);

    void sendMessage(String message, MessageType messageType) throws Exception;
}
