package edu.wirch.woody.bitbucket.conf;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class Action extends HttpServlet {
    private static final long serialVersionUID = 5349547414110768110L;

    private final LoginUriProvider loginUriProvider;
    private final Presenter presenter;

    @Autowired
    public Action(@ComponentImport final LoginUriProvider loginUriProvider, final Presenter presenter) {
        this.loginUriProvider = loginUriProvider;
        this.presenter = presenter;
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        setContentType(resp);
        if (!presenter.get(resp.getWriter())) {
            redirectToLogin(req, resp);
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        setContentType(resp);
        presenter.set(toStringMap(req), resp.getWriter());
    }


    private void setContentType(final HttpServletResponse resp) {
        resp.setContentType("text/html; charset=UTF-8");
    }

    private Map<String, String> toStringMap(final HttpServletRequest req) {
        final Map<String, String> map = new HashMap<>();
        for (final String name : Collections.list((Enumeration<String>) req.getParameterNames())) {
            map.put(name, req.getParameter(name));
        }
        return map;
    }


    private void redirectToLogin(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request))
                                              .toASCIIString());
    }

    private URI getUri(final HttpServletRequest request) {
        final StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }
}
