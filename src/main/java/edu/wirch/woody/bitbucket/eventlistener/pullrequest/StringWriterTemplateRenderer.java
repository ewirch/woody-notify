package edu.wirch.woody.bitbucket.eventlistener.pullrequest;

import com.atlassian.templaterenderer.TemplateRenderer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class StringWriterTemplateRenderer {
    private final TemplateRenderer templateRenderer;

    public StringWriterTemplateRenderer(final TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    public String render(final Map<String, Object> context, final String template) {
        final StringWriter writer = new StringWriter();
        try {
            templateRenderer.render(template, context, writer);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }
}