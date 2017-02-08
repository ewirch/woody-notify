package edu.wirch;

//
//import org.glassfish.jersey.client.ClientConfig;
//
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import edu.wirch.woody.chatservice.slack.SlackMessage;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import javax.ws.rs.core.MediaType;

public class JerseyTest {


    public static final String SLACK_WEBHOOK = "https://hooks.slack.com/services/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";
    public static final String MATTERMOST_WEBHOOK = "http://192.168.2.179:8065/hooks/75hxfzc8yjbjj84b3pn1cg9yoh";

    public static void main(String[] args) {
        new JerseyTest().sendNotification();
    }

    public void sendNotification() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses().add(JacksonJsonProvider.class);

//        final Client client = ClientBuilder.newClient(new ClientConfig());
//        final WebTarget target = client.target(getBaseURI());
        final SlackMessage.Attachment attachment = SlackMessage.Attachment.builder()
                                                                          .author_icon("http://www.logodesignlove.com/images/negative/wwf-logo-design.jpg")
                                                                          .author_name("Bitbucket")
                                                                          .color("#36a64f")
                                                                          .text("attachment text")
                                                                          .thumb_url("http://www.logodesignlove.com/images/negative/wwf-logo-design.jpg")

                                                                          .build();
        final SlackMessage message = SlackMessage.builder()
                                                 .userName(SlackMessage.DEFAULT_USER_NAME)
                                                 .icon_url(SlackMessage.DEFAULT_ICON_URL)
                                                 .channel("@ew")
                                                 .text("Link: <https://api.slack.com|Slack API>, ```pre```, `code`, _italic_, *bold*, ~strike~")
                                                 .attachment(attachment)
                                                 .build();
//        final Response response = target.request()
//                                        .accept(MediaType.APPLICATION_JSON)
//                                        .post(Entity.entity(message, MediaType.APPLICATION_JSON), Response.class);
//
//        System.out.println(response);
//        System.out.println(response.readEntity(String.class));

        final Client client = Client.create(clientConfig);
        final WebResource resource = client.resource(getBaseURI());

        final ClientResponse response = resource.accept(MediaType.TEXT_PLAIN).entity(message, MediaType.APPLICATION_JSON_TYPE)
                                                .post(ClientResponse.class);
        System.out.println(response);
        System.out.println(response.getEntity(String.class));
    }

    private String getBaseURI() {
        return SLACK_WEBHOOK;
    }

}
