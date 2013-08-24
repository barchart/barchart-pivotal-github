package com.barchart.web.site;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.web.util.Util;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Github webhook service.
 */
public class Github extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(Github.class);

	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		final PrintWriter writer = response.getWriter();

		writer.println("Github");

	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		final PrintWriter writer = response.getWriter();

		writer.println("Github");

		final String githubEvent = request.getHeader("X-GitHub-Event");
		log.info("githubEvent : {}", githubEvent);

		final String paytext = Util.consume(request);

		final Config payload = ConfigFactory.parseString(paytext);

		switch (githubEvent) {
		case "issues":
			processIssues(payload);
			break;
		default:
			break;
		}

	}

	/**
	 * Pattern to extract user and project.
	 */
	// "url": "https://api.github.com/repos/octocat/Hello-World/issues/1347"
	static final Pattern RX_URL = Pattern
			.compile("repos/([^/]+)/([^/]+)/issues");

	// http://developer.github.com/v3/activity/events/types/#issuesevent
	// http://developer.github.com/v3/issues/#get-a-single-issue
	protected void processIssues(final Config payload) {

		final String action = payload.getString("action");

		final Config issue = payload.getConfig("issue");

		final String url = issue.getString("url");
		final String title = issue.getString("title");
		final String number = issue.getString("number");

		final Matcher matcher = RX_URL.matcher(url);
		matcher.find();
		final String user = matcher.group(1);
		final String project = matcher.group(2);

		ensureProject(project);

		switch (action) {
		case "opened":
		case "reopened":
			log.info("opened: {} {}", project, number);
			break;
		case "closed":
			log.info("closed: {} {}", project, number);
			break;
		default:
			return;
		}

	}

	protected void ensureProject(final String project) {

	}

}
