package org.asuki.dp.other.callback;

import java.lang.reflect.Method;

public class MessageServiceImpl implements MessageService {

    private MessageCallback messageCallback;

    @Override
    public void registerMessageCallback(MessageCallback callback) {
        messageCallback = callback;
    }

    @Override
    public void sendMessage(String message, MessageType messageType)
            throws Exception {

        for (Method method : messageCallback.getClass().getMethods()) {
            if (!method.isAnnotationPresent(Message.class)) {
                continue;
            }

            Message messageAnnotation = method.getAnnotation(Message.class);

            if (messageAnnotation.type() == messageType) {
                method.invoke(messageCallback, message);
            }
        }
    }
}
