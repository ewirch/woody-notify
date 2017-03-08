package edu.wirch.woody.bitbucket.test;

import com.sun.jersey.api.client.ClientHandlerException;
import edu.wirch.woody.chatservice.ChatServiceFactory;
import edu.wirch.woody.chatservice.SendProblemException;
import edu.wirch.woody.chatservice.Target;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.PrintWriter;
import java.util.Map;

@Component
@ParametersAreNonnullByDefault
@Slf4j
public class TestMessagePresenter {
    private final ChatServiceFactory chatServiceFactory;

    @Autowired
    public TestMessagePresenter(final ChatServiceFactory chatServiceFactory) {
        this.chatServiceFactory = chatServiceFactory;
    }

    boolean onPost(final Map<String, String> params, final PrintWriter writer) {
        assert params != null;

        final String webhook = params.get("webhook");
        final String target = params.get("target");

        assert webhook != null;
        assert target != null;

        try {
            chatServiceFactory.createTestService(webhook)
                              .testSend(Target.ofEscaped(target), "Woody-Notify is properly connected.");
        } catch (final ClientHandlerException ex) {
            if (ex.getCause() != null) {
                writer.println(String.format("%s: %s", ex.getCause()
                                                        .getClass()
                                                        .getCanonicalName(), ex.getCause()
                                                                               .getMessage()));
            }
            log.error("Error sending test message for '{}' to '{}'.", target, webhook, ex);
            return false;
        } catch (final SendProblemException ex) {
            writer.println(ex.getMessage());
            return false;
        } catch (final RuntimeException ex) {
            log.error("Error sending test message for '{}' to '{}'.", target, webhook, ex);
            writer.println(ex.getClass()
                             .getCanonicalName() + ":" + ex.getMessage());
            return false;
        }
        return true;
    }

}
