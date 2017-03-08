package edu.wirch.woody.bitbucket.eventlistener.pullrequest;

import com.atlassian.bitbucket.event.pull.PullRequestParticipantReviewedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestParticipantStatusUpdatedEvent;
import com.atlassian.bitbucket.event.pull.PullRequestParticipantUnapprovedEvent;
import com.atlassian.bitbucket.nav.NavBuilder;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestParticipantStatus;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import edu.wirch.woody.chatservice.ChatServiceFactory;
import edu.wirch.woody.chatservice.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@Component
public class PullRequestApprovedListener {
    private static final String BASE_PATH = "/edu/wirch/woody/bitbucket/notification/pullrequest/";

    private final NavBuilder navBuilder;
    private final ChatServiceFactory chatServiceFactory;
    private final StringWriterTemplateRenderer templateRenderer;

    @Autowired
    public PullRequestApprovedListener(@ComponentImport final NavBuilder navBuilder,
                                       final ChatServiceFactory chatServiceFactory,
                                       @ComponentImport final TemplateRenderer templateRenderer) {
        this.navBuilder = navBuilder;
        this.chatServiceFactory = chatServiceFactory;
        this.templateRenderer = new StringWriterTemplateRenderer(templateRenderer);
    }


    @EventListener
    public void onUnapproved(final PullRequestParticipantUnapprovedEvent event) {
        onStatusChanged(event);
    }

    @EventListener
    public void onReviewed(final PullRequestParticipantReviewedEvent event) {
        onStatusChanged(event);
    }

    private void onStatusChanged(final PullRequestParticipantStatusUpdatedEvent event) {
        final Map<String, Object> context = populateContext(event);
        final String prAuthor = event.getPullRequest()
                                     .getAuthor()
                                     .getUser()
                                     .getName();

        final String text;
        final PullRequestParticipantStatus status = event.getParticipant()
                                                         .getStatus();
        //noinspection IfStatementWithTooManyBranches
        if (status == PullRequestParticipantStatus.APPROVED) {
            text = templateRenderer.render(context, BASE_PATH + "approved.vm");
        } else if (status == PullRequestParticipantStatus.UNAPPROVED && event.getPreviousStatus() == PullRequestParticipantStatus.APPROVED) {
            text = templateRenderer.render(context, BASE_PATH + "unapproved.vm");
        } else if (status == PullRequestParticipantStatus.NEEDS_WORK) {
            text = templateRenderer.render(context, BASE_PATH + "needswork.vm");
        } else {
            context.put("action", status.toString());
            text = templateRenderer.render(context, BASE_PATH + "needsworkRevoked.vm");
        }
        chatServiceFactory.create()
                          .send(Target.ofUser(prAuthor), text);
    }

    @Nonnull
    private Map<String, Object> populateContext(final PullRequestParticipantStatusUpdatedEvent event) {
        final PullRequest pr = event.getPullRequest();
        final Repository repository = pr.getToRef()
                                        .getRepository();
        final Project project = repository.getProject();

        final Map<String, Object> context = new HashMap<>();
        context.put("prUrl", navBuilder.project(project)
                                       .repo(repository)
                                       .pullRequest(pr.getId())
                                       .overview()
                                       .buildAbsolute());
        context.put("prName", pr.getTitle());
        context.put("reviewers", pr.getReviewers()
                                   .size());
        context.put("reviewer", event.getParticipant()
                                     .getUser()
                                     .getDisplayName());
        return context;
    }

}