package edu.wirch.woody.bitbucket.conf;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ActionSupport implements Serializable {
	private static final long serialVersionUID = -6666091457396539634L;

	public Map<String, String> toStringMap(final HttpServletRequest req) {
		final Map<String, String> map = new HashMap<>();
		for (final String name : Collections.list((Enumeration<String>) req.getParameterNames())) {
			map.put(name, req.getParameter(name));
		}
		return map;
	}

	public void setContentType(final ServletResponse resp) {
		resp.setContentType("text/html; charset=UTF-8");
	}
}