package edu.wirch.woody.chatservice.slack;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import edu.wirch.woody.chatservice.ChatService;
import edu.wirch.woody.chatservice.SendProblemException;
import edu.wirch.woody.chatservice.Target;
import edu.wirch.woody.chatservice.TestChatService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MediaType;

@Slf4j
public class SlackChatService implements ChatService, TestChatService {
//  https://hooks.slack.com/services/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
    private final String webhook;
    private final ReturnCodeMessageMapper messageMapper;

    public SlackChatService(final String webhook) {
        this.webhook = webhook;
        messageMapper = new ReturnCodeMessageMapper();
    }

    @Override
    public void send(final Target target, final String text) {
        final SlackMessage message = SlackMessage.defaultValues()
                                                 .userName("Woody-Notify")
                                                 .icon_url("https://maxcdn.icons8.com/Color/PNG/48/Cinema/woody_woodpecker-48.png")
                                                 .channel(target.getTarget())
                                                 .text(text)
                                                 .build();
        final WebResource resource = getDefaultClient().resource(webhook);

        final ClientResponse response = resource.accept(MediaType.TEXT_PLAIN)
                                                .entity(message, MediaType.APPLICATION_JSON_TYPE)
                                                .post(ClientResponse.class);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            throw new SendProblemException(messageMapper.map(response.getStatus(), response.getEntity(String.class), message));
        }
    }

    @Override
    public void testSend(final Target target, final String text) {
        send(target, text);
    }

    @Nonnull
    private Client getDefaultClient() {
        final ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses()
                    .add(JacksonJsonProvider.class);

        final Client client = Client.create(clientConfig);
        client.setConnectTimeout(10 * 1000);
        client.setReadTimeout(10 * 1000);
        return client;
    }

    private String toSlackUserString(final String user) {
        return "@" + user;
    }
}
