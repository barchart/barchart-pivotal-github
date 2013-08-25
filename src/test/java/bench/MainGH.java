package bench;

import java.util.List;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.github.RepositoryServiceExtra;
import com.barchart.web.util.UtilGH;
import com.barchart.web.util.UtilSyncIsstory;

public class MainGH {

	private static final Logger log = LoggerFactory.getLogger(MainGH.class);

	public static void main(final String... args) throws Exception {

		mainSyncIsstory();

	}

	public static void mainSyncIsstory(final String... args) throws Exception {

		UtilSyncIsstory.linkIssueStoryAll();
	}

	public static void mainWebHook(final String... args) throws Exception {

		UtilGH.ensureWebhookAll();

		final RepositoryServiceExtra service = UtilGH.repositoryService();

		final IRepositoryIdProvider repository = service.getRepository(
				"barchart", "barchart-http");

		final List<RepositoryHook> hookList = service.getHooks(repository);

		for (final RepositoryHook hook : hookList) {
			log.info("hook.name: " + hook.getName());
			log.info("hook.url : " + hook.getUrl());
			log.info("hook.conf: " + hook.getConfig());
		}

	}

}
