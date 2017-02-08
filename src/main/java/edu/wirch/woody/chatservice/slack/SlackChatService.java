package edu.wirch.woody.chatservice.slack;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import edu.wirch.woody.chatservice.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MediaType;

@Slf4j
public class SlackChatService implements ChatService {

    private final String webhook;

    public SlackChatService(final String webhook) {
        this.webhook = webhook;
    }

    @Override
    public void send(final String user, final String text) {
        log.info("Sending to {}", toSlackUserString(user));
        final SlackMessage message = SlackMessage.defaultValues()
                                                 .userName("Woody-Notify")
                                                 .icon_url("https://maxcdn.icons8.com/Color/PNG/48/Cinema/woody_woodpecker-48.png")
                                                 .channel(toSlackUserString(user))
                                                 .text(text)
                                                 .build();
        final WebResource resource = getDefaultClient().resource(webhook);

        final ClientResponse response = resource.accept(MediaType.TEXT_PLAIN)
                                                .entity(message, MediaType.APPLICATION_JSON_TYPE)
                                                .post(ClientResponse.class);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            log.error("Client returned status {} ({}): {}", response.getStatus(), response.getClientResponseStatus()
                                                                                          .getReasonPhrase(), response.getEntity(String.class));
        }
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
