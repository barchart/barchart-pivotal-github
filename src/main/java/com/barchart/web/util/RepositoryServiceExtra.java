package com.barchart.web.util;

import static org.eclipse.egit.github.core.client.IGitHubConstants.*;

import java.io.IOException;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryHook;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

/**
 * Extend to reflect new Github API.
 */
public class RepositoryServiceExtra extends RepositoryService {

	public RepositoryServiceExtra(final GitHubClient client) {
		super(client);
	}

	/**
	 * Create hook in repository
	 * 
	 * @param repository
	 * @param hook
	 * @return created repository hook
	 * @throws IOException
	 */
	@Override
	public RepositoryHookExtra createHook(
			final IRepositoryIdProvider repository, final RepositoryHook hook)
			throws IOException {
		final String id = getId(repository);
		final StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_HOOKS);
		return client.post(uri.toString(), hook, RepositoryHookExtra.class);
	}

}
