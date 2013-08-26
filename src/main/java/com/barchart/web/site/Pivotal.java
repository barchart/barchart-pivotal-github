package com.barchart.web.site;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.pivotal.hook.Event;
import com.barchart.pivotal.model.Story;
import com.barchart.pivotal.service.PivotalService;
import com.barchart.pivotal.util.UtilGson;
import com.barchart.web.heroku.RunnableCore;
import com.barchart.web.heroku.ThreadFactory;
import com.barchart.web.util.Util;
import com.barchart.web.util.UtilGH;
import com.barchart.web.util.UtilPT;
import com.barchart.web.util.UtilSyncIsstory;
import com.typesafe.config.Config;

/**
 * Pivotal webhook rest service.
 */
public class Pivotal extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final ExecutorService executor = Executors
			.newFixedThreadPool(1, new ThreadFactory("pivotal"));

	private static final Logger log = LoggerFactory.getLogger(Pivotal.class);

	private static final Config reference = Util.reference();

	private static final String secret = reference.getString("pivotal.secret");

	@Override
	protected void doGet(final HttpServletRequest requset,
			final HttpServletResponse response) throws ServletException,
			IOException {

		final PrintWriter writer = response.getWriter();

		writer.println("Pivotal");

		log.info("Pivotal");

	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		if (!secret.equals(request.getParameter("secret"))) {
			log.error("invalid secret");
			return;
		}

		final Event event = UtilGson.getGson().fromJson(request.getReader(),
				Event.class);

		log.info("event: {}", event.kind);

		switch (event.kind) {
		case "comment_create_activity":
			// TODO
			break;
		case "comment_delete_activity":
			// TODO
			break;
		case "story_move_activity":
			// TODO
			break;
		case "story_update_activity":
			processStoryUpdate(event);
			break;
		default:
			break;
		}

	}

	protected void processStoryUpdate(final Event event) throws IOException {
		executor.submit(new RunnableCore("processStoryUpdate") {
			@Override
			protected void runCore() throws Exception {

				/** Verify project has github integration. */

				final int projectId = event.project.id;

				final String integrationName = reference
						.getString("pivotal.integration.name");

				final Integer integrationId = UtilPT.integration(projectId,
						integrationName);

				if (integrationId == null) {
					log.warn("project has no github integration: {}", projectId);
					return;
				}

				/** Verify story has github integration. */

				final PivotalService pivotal = UtilPT.pivotalService();

				final int storyId = event.storyResource().id;

				final Story story = pivotal.story(projectId, storyId);

				if (!integrationId.equals(story.integration_id)) {
					log.warn("story has no github integration: {}", storyId);
					return;
				}

				/** Update github issue. */

				// barchart/barchart-http/issues/2
				final String[] termArray = story.external_id.split("/");

				final String githubUser = termArray[0];
				final String githubName = termArray[1];
				final String issueNumber = termArray[3];

				final IssueService issueService = UtilGH.issueService();

				final Issue issue = issueService.getIssue(githubUser,
						githubName, issueNumber);

				UtilSyncIsstory.apply(story, issue);

				issueService.editIssue(githubUser, githubName, issue);

				log.info("updated issue: {}", story.external_id);

			}
		});
	}

}
