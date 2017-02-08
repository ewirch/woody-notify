package edu.wirch.woody.chatservice;

public class NopChatService implements ChatService {
    @Override
    public void send(final String user, final String text) {
        // intentionally left blank
    }
}
