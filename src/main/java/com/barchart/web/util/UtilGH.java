package com.barchart.web.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryHook;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.github.MilestoneServiceExtra;
import com.barchart.github.RepositoryHookExtra;
import com.barchart.github.RepositoryServiceExtra;
import com.typesafe.config.Config;

/**
 * Github rest-api utilities.
 */
public class UtilGH {

	/**
	 * Pattern to extract user and project.
	 */
	// "url": "https://api.github.com/repos/octocat/Hello-World/issues/1347"
	public static final Pattern RX_URL = Pattern
			.compile("repos/([^/]+)/([^/]+)/issues");

	// https://github.com/github/github-services/blob/master/lib/services/web.rb
	final static String HOOK_NAME = "web";

	// http://developer.github.com/v3/activity/events/types/
	// https://github.com/github/github-services/blob/master/lib/service.rb#L79
	static final String[] KNOWN_EVENTS = new String[] { //
	//
			"push", //
			"commit_comment", //
			//
			"pull_request", //
			"pull_request_review_comment", //
			//
			"issues", //
			"issue_comment", //
			//
			"create", //
			"delete", //
			"download", //
			"member", //
			"team_add", //
			"fork", //
			"fork_apply", //
			"gist", //
			"gollum", //
			"public", //
			"follow", //
			"watch", //
			"status", //

	};

	private static final Logger log = LoggerFactory.getLogger(UtilGH.class);

	private static final ThreadLocal<GitHubClient> CLIENT_REST = new ThreadLocal<GitHubClient>() {
		@Override
		protected GitHubClient initialValue() {

			log.info("client for thread: {}", Thread.currentThread().getName());

			final Config reference = Util.reference();

			final String username = reference.getString("github.username");
			final String password = reference.getString("github.password");

			final GitHubClient client = new GitHubClient();

			client.setCredentials(username, password);

			return client;

		}
	};

	/**
	 * New github rest-api client
	 */
	public static GitHubClient clientRest() {
		return CLIENT_REST.get();
	}

	/**
	 * Verify github webhook presence in the list by name and config match.
	 */
	public static boolean contains(final List<RepositoryHook> hookList,
			final RepositoryHook item) {

		for (final RepositoryHook hook : hookList) {
			if (equals(hook, item)) {
				return true;
			}
		}

		return false;

	}

	/**
	 * Create new or replace old hook.
	 */
	public static void ensureWebhook(final RepositoryServiceExtra service,
			final Repository repository, final String url, final String secret)
			throws IOException {

		final List<RepositoryHook> hookList = service.getHooks(repository);

		for (final RepositoryHook hook : hookList) {
			final String name = hook.getName();
			if (HOOK_NAME.equals(name)) {
				log.info("hook delete: {}", name);
				service.deleteHook(repository, (int) hook.getId());
			}
		}

		final RepositoryHook hook1 = webhook(url, secret);

		final RepositoryHookExtra hook2 = service.createHook(repository, hook1);

		log.info("hook create: {}", hook2.getName());

	}

	/**
	 * Create github web service hooks for all projects.
	 */
	public static void ensureWebhookAll() throws IOException {

		final Config reference = Util.reference();

		final String owner = reference.getString("github.owner");
		final String secret = reference.getString("github.secret");
		final String webhook = reference.getString("github.webhook");

		final List<? extends Config> projectList = reference
				.getConfigList("project-list");

		final RepositoryServiceExtra service = repositoryService();

		for (final Config project : projectList) {

			final String name = project.getString("github.name");

			log.info("project: {}", name);

			final Repository repository = service.getRepository(owner, name);

			ensureWebhook(service, repository, webhook, secret);

		}

	}

	public static boolean equals(final RepositoryHook one,
			final RepositoryHook two) {

		if (!one.getName().equals(two.getName())) {
			return false;
		}

		final Map<String, String> hookConfig = one.getConfig();
		final Map<String, String> itemConfig = two.getConfig();

		if (!hookConfig.equals(itemConfig)) {
			return false;
		}

		return true;

	}

	public static RepositoryServiceExtra repositoryService() {
		return new RepositoryServiceExtra(clientRest());
	}

	public static MilestoneServiceExtra milestoneService() {
		return new MilestoneServiceExtra(clientRest());
	}

	public static IssueService issueService() {
		return new IssueService(clientRest());
	}

	/**
	 * Create default github webhook bean.
	 */
	// https://github.com/github/github-services/blob/master/lib/services/web.rb
	public static RepositoryHookExtra webhook(final String url,
			final String secret) {

		final Map<String, String> config = new HashMap<String, String>();
		config.put("url", url); // target for http post
		config.put("secret", secret); // enable hmac verification
		config.put("content_type", "json"); // ensure body encoding
		config.put("ssl_version", "3"); // use latest ssl protocol
		config.put("insecure_ssl", "1"); // ignore ssl certificate mismatch

		final RepositoryHookExtra hook = new RepositoryHookExtra();
		hook.setActive(true);
		hook.setName(HOOK_NAME);
		hook.setConfig(config);
		hook.setEvents(KNOWN_EVENTS);

		return hook;

	}

}
