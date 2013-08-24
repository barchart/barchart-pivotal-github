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

	private static final Logger log = LoggerFactory.getLogger(Init.class);

	/**
	 * Create github web service hookes if missing.
	 */
	public static final void ensureGithubWebhooks() throws IOException {

		final RepositoryService service = Util.githubRepositoryService();

		final Config reference = Util.reference();

		final String owner = reference.getString("github.owner");

		final String pathRoot = reference.getString("heroku.application.url");
		final String pathGithub = reference
				.getString("heroku.application.path.github");

		final List<? extends Config> projectList = reference
				.getConfigList("project-list");

		final String pathWebhook = pathRoot + pathGithub;

		for (final Config project : projectList) {

			final String name = project.getString("name");

			final IRepositoryIdProvider repository = service.getRepository(
					owner, name);

			final List<RepositoryHook> hookList = service.getHooks(repository);

			final RepositoryHook request = githubWebhook(pathWebhook);

			if (contains(hookList, request)) {
				log.info("hook present: {}", name);
				continue;
			} else {
				service.createHook(repository, request);
				log.info("hook created: {}", name);
			}

		}

	}

	/**
	 * Create default github webhook bean.
	 */
	public static RepositoryHook githubWebhook(final String url) {

		final Map<String, String> config = new HashMap<String, String>();
		config.put("url", url);
		config.put("content_type", "json");
		config.put("ssl_version", "3");

		final RepositoryHook hook = new RepositoryHook();
		hook.setActive(true);
		hook.setName("web");
		hook.setConfig(config);

		return hook;

	}

	/**
	 * Verify github webhook hook presence in the list by name and config match.
	 */
	public static boolean contains(final List<RepositoryHook> hookList,
			final RepositoryHook item) {

		for (final RepositoryHook hook : hookList) {

			if (!hook.getName().equals(item.getName())) {
				continue;
			}

			final Map<String, String> hookConfig = hook.getConfig();
			final Map<String, String> itemConfig = item.getConfig();

			if (!hookConfig.equals(itemConfig)) {
				continue;
			}

			return true;
		}

		return false;

	}

}
