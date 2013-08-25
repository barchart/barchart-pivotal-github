package com.barchart.web.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.github.MilestoneExtra;
import com.barchart.github.MilestoneServiceExtra;
import com.barchart.github.RepositoryServiceExtra;
import com.barchart.pivotal.model.Comment;
import com.barchart.pivotal.model.Epic;
import com.barchart.pivotal.model.Label;
import com.barchart.pivotal.service.PivotalService;
import com.barchart.pivotal.util.UtilGson;
import com.typesafe.config.Config;

/**
 * Github/Milestone <-> Pivotal/Epic synchronization utilities.
 */
public class UtilSyncMilepic {

	static class Context {

		final MilestoneServiceExtra milestoneService;

		final PivotalService pivotal;
		final Config project;

		final Config reference = Util.reference();
		final RepositoryServiceExtra repositoryService;

		Context(final Config project) throws IOException {

			this.project = project;

			this.pivotal = UtilPT.pivotalService();

			this.milestoneService = UtilGH.milestoneService();
			this.repositoryService = UtilGH.repositoryService();

		}

	}

	private static final Logger log = LoggerFactory
			.getLogger(UtilSyncMilepic.class);

	/**
	 * Apply epic to milestone .
	 */
	public static void apply(final Epic epic, final MilestoneExtra milestone) {

		if (epic.description == null) {
			epic.description = "";
		}

		final String name = milepicName(epic.name, epic.id);
		final String description = epic.description;

		milestone.setTitle(name);
		milestone.setDescription(description);

	}

	/**
	 * Apply milestone to epic.
	 */
	public static void apply(final MilestoneExtra milestone, final Epic epic) {

		if (epic.description == null) {
			epic.description = "";
		}

		final String name = milepicName(milestone.getTitle(), epic.id);
		final String description = milestone.getDescription();

		epic.name = name;
		epic.description = description;
		epic.comments = null;

	}

	/**
	 * Find epic sync comment.
	 */
	public static Comment comment(final Epic epic) {
		if (epic.comments == null) {
			return null;
		}
		for (final Comment comment : epic.comments) {
			if (epicSync(comment) != null) {
				return comment;
			}
		}
		return null;
	}

	/**
	 * Epic header list.
	 */
	public static List<Epic> epicList(final Context context) throws IOException {

		final int pivotalId = context.project.getInt("pivotal.id");

		final List<Epic> epicList = context.pivotal.epicList(pivotalId,
				Epic.FIELD_HEAD);

		return epicList;

	}

	public static SyncLink epicSync(final Comment comment) {
		try {
			final SyncLink bean = UtilGson.getGson().fromJson(comment.text,
					SyncLink.class);
			if (bean.github == null) {
				return null;
			}
			return bean;
		} catch (final Throwable e) {
			return null;
		}
	}

	/**
	 * Verify match of milestone to epic.
	 */
	public static boolean isMatch(final Context context,
			final MilestoneExtra milestone, final Epic epic) throws IOException {

		final String githubSource = milestoneURL(context, milestone);
		final String githubTarget = milestoneURL(context, epic);

		return githubSource.equals(githubTarget);

	}

	public static void linkMilestoneEpic(final Context context)
			throws IOException {

		final int githubId = context.project.getInt("github.id");
		final String githubUser = context.reference.getString("github.owner");
		final String githubName = context.project.getString("github.name");

		final int pivotalId = context.project.getInt("pivotal.id");
		final String pivotalName = context.project.getString("pivotal.name");

		final String state = null;
		final List<MilestoneExtra> milestoneList = context.milestoneService
				.getMilestonesExtra(githubUser, githubName, state);

		final List<Epic> epicList = milepicSearch(context);

		for (final MilestoneExtra milestone : milestoneList) {

			log.info("milestone : {}", milestone.getUrl());

			final Epic epic = milepicMatch(context, milestone, epicList);

			if (epic == null) {
				milepicCreate(context, milestone);
			} else {
				milepicUpdate(context, milestone, epic);
			}

		}

	}

	/**
	 * 
	 */
	public static void linkMilestoneEpicAll() throws IOException {

		final Config reference = Util.reference();

		final List<? extends Config> projectList = reference
				.getConfigList("project-list");

		for (final Config project : projectList) {

			final Context context = new Context(project);

			linkMilestoneEpic(context);

		}

	}

	/**
	 * Create epic linked to milestone.
	 */
	public static Epic milepicCreate(final Context context,
			final MilestoneExtra milestone) throws IOException {

		final int pivotalId = context.project.getInt("pivotal.id");

		final Label label = new Label();
		label.name = milepicDrop(milestone.getTitle());

		/** Epic 1. */
		final Epic epic1 = new Epic();
		epic1.project_id = pivotalId;
		epic1.name = "epic-" + System.currentTimeMillis();
		epic1.label = label;
		log.info("epic1 : {}", epic1);

		/** Epic 2. */
		final Epic epic2 = context.pivotal.epicCreate(epic1);
		apply(milestone, epic2);
		//
		final SyncLink sync = new SyncLink();
		sync.github = GooGl.shortURL(milestoneURL(context, milestone));
		sync.pivotal = GooGl.shortURL(epic2.url);
		epic2.description = sync + epic2.description;
		//
		log.info("epic2 : {}", epic2);

		/** Epic 3. */
		final Epic epic3 = context.pivotal.epicUpdate(epic2);
		log.info("epic3 : {}", epic3);

		return epic3;

	}

	/**
	 * Remove mileston/epic name pattern.
	 */
	public static String milepicDrop(final String name) {
		return name.replaceAll("P\\d+T", "").trim();
	}

	/**
	 * Find epic matching the milestone.
	 */
	public static Epic milepicMatch(final Context context,
			final MilestoneExtra milestone, final List<Epic> epicList)
			throws IOException {
		for (final Epic epic : epicList) {
			if (isMatch(context, milestone, epic)) {
				return epic;
			}
		}
		return null;
	}

	/**
	 * Ensure mileston/epic name pattern.
	 */
	public static String milepicName(final String name, final int id) {
		return milepicDrop(name) + " " + "P" + id + "T";
	}

	/**
	 * Discover epics with links.
	 */
	public static List<Epic> milepicSearch(final Context context)
			throws IOException {

		final List<Epic> list = new ArrayList<Epic>();

		for (final Epic item : epicList(context)) {

			/** Epic with all fields. */
			final Epic epic = context.pivotal.epic(item.project_id, item.id);

			final String line = SyncLink.search(epic.description);

			if (line == null) {
				continue;
			}

			list.add(epic);

		}

		return list;
	}

	/**
	 * Sync milestone/epic if due.
	 */
	public static void milepicUpdate(final Context context,
			final MilestoneExtra milestone, final Epic epic) throws IOException {

		final long githubTime = milestone.getUpdatedAt().getTime();
		final long pivotalTime = epic.updated_at.getMillis();

		final String mileName = milestone.getTitle();
		final String epicName = epic.name;

		final String mileDesc = milestone.getDescription();
		final String epicDesc = epic.description;

		final boolean isName = !mileName.equals(epicName);
		final boolean isDesc = !mileDesc.equals(epicDesc);

		final boolean isAny = isName || isDesc;

		if (!isAny) {
			log.info("no change");
			return;
		}

		log.info("change: isName={}; isDesc={};", isName, isDesc);

		log.info("githubTime={}; pivotalTime={}", new DateTime(githubTime),
				new DateTime(pivotalTime));

		if (githubTime > pivotalTime) {

			log.info("apply: milestone -> epic");

			apply(milestone, epic);

			final Epic result = context.pivotal.epicUpdate(epic);

			log.debug("result: {}", result);

		} else {

			log.info("apply: epic -> milestone");

			apply(epic, milestone);

			final String user = context.reference.getString("github.owner");
			final String name = context.project.getString("github.name");

			final Repository repository = context.repositoryService
					.getRepository(user, name);

			final MilestoneExtra result = context.milestoneService
					.editMilestone(repository, milestone);

			log.debug("result: {}", result);

		}

	}

	/**
	 * Github HTTP url from milestone REST data.
	 */
	// https://github.com/barchart/barchart-http/issues?milestone=1
	public static String milestoneURL(final Context context,
			final MilestoneExtra milestone) {
		final String user = context.reference.getString("github.owner");
		final String name = context.project.getString("github.name");
		return "https://github.com/" + user + "/" + name + "/issues?milestone="
				+ milestone.getNumber();
	}

	/**
	 * Github HTTP url from epic REST data.
	 */
	public static String milestoneURL(final Context context, final Epic epic)
			throws IOException {
		final SyncLink sync = SyncLink.decode(epic.description);
		if (sync == null) {
			return null;
		}
		return GooGl.longURL(sync.github);
	}

}
