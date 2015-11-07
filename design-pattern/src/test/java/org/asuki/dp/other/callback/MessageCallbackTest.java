package org.asuki.dp.other.callback;

import org.testng.annotations.Test;

public class MessageCallbackTest {

    @Test
    public void testCallback() throws Exception {
        MessageService messageService = new MessageServiceImpl();
        messageService.registerMessageCallback(new MessageCallback());

        messageService.sendMessage("Normal message...", MessageType.NORMAL);

        messageService.sendMessage("Urgent message...", MessageType.URGENT);
    }

}
