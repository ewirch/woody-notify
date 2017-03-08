package edu.wirch.woody.chatservice;

public class SendProblemException extends RuntimeException {
    private static final long serialVersionUID = 1546905521191738714L;

    public SendProblemException(final String message) {
        super(message);
    }
}
