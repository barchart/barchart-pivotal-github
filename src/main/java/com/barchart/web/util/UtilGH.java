package com.barchart.web.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryHook;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;

/**
 * Github utilities.
 */
public class UtilGH {

	static String HOOK_NAME = "web";

	// https://github.com/github/github-services/blob/master/lib/service.rb#L79
	static final String[] KNOWN_EVENTS = new String[] { //
	//
			"create", //
			"delete", //
			"commit_comment", //
			"download", //
			"follow", //
			"fork", //
			"fork_apply", //
			"gist", //
			"gollum", //
			"issues", //
			"issue_comment", //
			"member", //
			"public", //
			"pull_request", //
			"push", "team_add", //
			"watch", //
			"pull_request_review_comment", //
			"status", //

	};

	private static final Logger log = LoggerFactory.getLogger(UtilGH.class);

	public static GitHubClient clientGithubAPI() {

		final Config config = Util.reference();

		final String username = config.getString("github.username");
		final String password = config.getString("github.password");

		final GitHubClient client = new GitHubClient();
		client.setCredentials(username, password);

		return client;

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
	public static void ensureGithubWebhook(
			final RepositoryServiceExtra service,
			final IRepositoryIdProvider repository, final String url,
			final String secret) throws IOException {

		final List<RepositoryHook> hookList = service.getHooks(repository);

		for (final RepositoryHook hook : hookList) {
			final String name = hook.getName();
			if (HOOK_NAME.equals(name)) {
				log.info("hook delete: {}", name);
				service.deleteHook(repository, (int) hook.getId());
			}
		}

		final RepositoryHook hook = githubWebhook(url, secret);

		service.createHook(repository, hook);
		log.info("hook create: {}", hook.getName());

	}

	/**
	 * Create github web service hooks if missing.
	 */
	public static final void ensureGithubWebhookAll() throws IOException {

		final RepositoryServiceExtra service = UtilGH.githubRepositoryService();

		final Config reference = Util.reference();

		final String owner = reference.getString("github.owner");
		final String secret = reference.getString("github.secret");

		final String pathRoot = reference.getString("heroku.application.url");
		final String pathGithub = reference
				.getString("heroku.application.path.github");

		final List<? extends Config> projectList = reference
				.getConfigList("project-list");

		final String pathWebhook = pathRoot + pathGithub;

		for (final Config project : projectList) {

			final String name = project.getString("name");

			log.info("project: {}", name);

			final IRepositoryIdProvider repository = service.getRepository(
					owner, name);

			ensureGithubWebhook(service, repository, pathWebhook, secret);

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

	public static RepositoryServiceExtra githubRepositoryService() {
		return new RepositoryServiceExtra(clientGithubAPI());
	}

	/**
	 * Create default github webhook bean.
	 */
	public static RepositoryHookExtra githubWebhook(final String url,
			final String secret) {

		// https://github.com/github/github-services/blob/master/lib/services/web.rb

		final Map<String, String> config = new HashMap<String, String>();
		config.put("url", url); // target for http post
		config.put("secret", secret); // enable hmac verification
		config.put("content_type", "json"); // ensure body encoding
		config.put("ssl_version", "3"); // use latest ssl protocol
		config.put("insecure_ssl", "1"); // ignore ssl certificate check

		final RepositoryHookExtra hook = new RepositoryHookExtra();
		hook.setActive(true);
		hook.setName(HOOK_NAME);
		hook.setConfig(config);
		hook.setEvents(KNOWN_EVENTS);

		return hook;

	}

}
