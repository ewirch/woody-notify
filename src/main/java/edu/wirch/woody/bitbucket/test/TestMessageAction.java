package edu.wirch.woody.bitbucket.test;

import edu.wirch.woody.bitbucket.conf.ActionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TestMessageAction extends HttpServlet {
    private static final long serialVersionUID = -82651348356264464L;

    private final ActionSupport actionSupport;
    private final TestMessagePresenter presenter;

    @Autowired
    public TestMessageAction(final TestMessagePresenter presenter) {
        this.presenter = presenter;
        actionSupport = new ActionSupport();
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        actionSupport.setContentType(resp);
        if (!presenter.onPost(actionSupport.toStringMap(req), resp.getWriter())) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
