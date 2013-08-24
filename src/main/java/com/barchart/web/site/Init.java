package com.barchart.web.site;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryHook;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;

/**
 * Configuration initializers.
 */
public class Init {

	static String HOOK_1 = "web";

	static String HOOK_2 = "lechat";

	// https://github.com/github/github-services/blob/master/lib/service.rb#L79
	static final String KNOWN_EVENTS = "create," + "delete," + //
			"commit_comment," + //
			"download," + //
			"follow," + //
			"fork," + //
			"fork_apply," + //
			"gist," + //
			"gollum," + //
			"issue_comment," + //
			"issues," + //
			"member," + //
			"public," + //
			"pull_request," + //
			"push," + //
			"team_add," + //
			"watch," + //
			"pull_request_review_comment," + //
			"status";

	private static final Logger log = LoggerFactory.getLogger(Init.class);

	/**
	 * Verify github webhook hook presence in the list by name and config match.
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

	public static void ensureGithubWebhook(final RepositoryService service,
			final IRepositoryIdProvider repository, final String url,
			final String secret) throws IOException {

		final List<RepositoryHook> hookList = service.getHooks(repository);

		for (final RepositoryHook hook : hookList) {
			final String name = hook.getName();
			final boolean isMatch = HOOK_1.equals(name) || HOOK_2.equals(name);
			if (isMatch) {
				log.info("delete: {}", name);
				service.deleteHook(repository, (int) hook.getId());
			}
		}

		// final RepositoryHook hook = githubWebhook1(url, secret);
		final RepositoryHook hook = githubWebhook2(url);

		service.createHook(repository, hook);
		log.info("create: {}", hook.getName());

	}

	/**
	 * Create github web service hookes if missing.
	 */
	public static final void ensureGithubWebhookAll() throws IOException {

		final RepositoryService service = Util.githubRepositoryService();

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

	/**
	 * Create default github webhook bean.
	 */
	public static RepositoryHook githubWebhook1(final String url,
			final String secret) {

		// https://github.com/github/github-services/blob/master/lib/services/web.rb

		final Map<String, String> config = new HashMap<String, String>();
		config.put("url", url); // post target
		config.put("secret", secret); // enable sha1
		config.put("content_type", "json"); // ensure encoding
		config.put("ssl_version", "3"); // use secure ssl
		config.put("insecure_ssl", "1"); // ignore ssl certificate check

		final RepositoryHook hook = new RepositoryHook();
		hook.setActive(true);
		hook.setName(HOOK_1);
		hook.setConfig(config);

		return hook;

	}

	/**
	 * Create default github webhook bean.
	 */
	public static RepositoryHook githubWebhook2(final String url) {

		// https://github.com/github/github-services/blob/master/lib/services/kato.rb

		final Map<String, String> config = new HashMap<String, String>();
		config.put("webhook_url", url); // post target

		final RepositoryHook hook = new RepositoryHook();
		hook.setActive(true);
		hook.setName(HOOK_2);
		hook.setConfig(config);

		return hook;

	}

}
