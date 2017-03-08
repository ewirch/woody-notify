package edu.wirch.woody.chatservice;

public class NopChatService implements ChatService {
	@Override
	public void send(final Target target, final String text) {
		// intentionally left blank
	}
}
