package com.barchart.github;

import static org.eclipse.egit.github.core.client.IGitHubConstants.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PagedRequest;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.MilestoneService;

import com.google.gson.reflect.TypeToken;

/**
 * Extend to reflect new Github API.
 */
public class MilestoneServiceExtra extends MilestoneService {

	public MilestoneServiceExtra(final GitHubClient client) {
		super(client);
	}

	@Override
	public MilestoneExtra editMilestone(final IRepositoryIdProvider repository,
			final Milestone milestone) throws IOException {

		final String repoId = getId(repository);

		if (milestone == null) {
			throw new IllegalArgumentException("Milestone cannot be null"); //$NON-NLS-1$

		}

		final StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(repoId);
		uri.append(SEGMENT_MILESTONES);
		uri.append('/').append(milestone.getNumber());

		return client.post(uri.toString(), milestone, MilestoneExtra.class);
	}

	public List<MilestoneExtra> getMilestonesExtra(final String user,
			final String repository, final String state) throws IOException {
		verifyRepository(user, repository);

		final String repoId = user + '/' + repository;
		return getMilestones(repoId, state);
	}

	private List<MilestoneExtra> getMilestones(final String id,
			final String state) throws IOException {

		final StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_MILESTONES);
		final PagedRequest<MilestoneExtra> request = createPagedRequest();
		if (state != null)
			request.setParams(Collections.singletonMap(
					IssueService.FILTER_STATE, state));
		request.setUri(uri).setType(new TypeToken<List<MilestoneExtra>>() {
		}.getType());
		return getAll(request);
	}

}
