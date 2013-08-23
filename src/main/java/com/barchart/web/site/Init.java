package com.barchart.web.site;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryHook;
import org.eclipse.egit.github.core.service.RepositoryService;

import com.typesafe.config.Config;

public class Init {

	public static final void initGithubWebhooks() throws IOException {

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

			final Map<String, String> config = new HashMap<String, String>();
			config.put("url", pathWebhook);
			config.put("content_type", "json");
			config.put("ssl_version", "3");

			final RepositoryHook request = new RepositoryHook();
			request.setActive(true);
			request.setName("web");
			request.setConfig(config);

			final RepositoryHook response = service.createHook(repository,
					request);

		}

	}

}
