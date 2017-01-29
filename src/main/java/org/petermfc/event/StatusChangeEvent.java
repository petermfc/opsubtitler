package org.petermfc.event;

public class StatusChangeEvent {
    private String messageText;

    public StatusChangeEvent(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }
}
