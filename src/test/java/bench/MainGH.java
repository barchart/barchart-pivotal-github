package bench;

import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.github.RepositoryHookExtra;
import com.barchart.github.RepositoryServiceExtra;
import com.barchart.web.util.UtilGH;
import com.barchart.web.util.UtilSyncIsstory;

public class MainGH {

	private static final Logger log = LoggerFactory.getLogger(MainGH.class);

	public static void main(final String... args) throws Exception {

		mainWebHook();

	}

	public static void mainSyncIsstory(final String... args) throws Exception {

		UtilSyncIsstory.linkIssueStoryAll();
	}

	public static void mainWebHook(final String... args) throws Exception {

		log.info("init");

		UtilGH.ensureWebhookAll();

		log.info("ready");

		final RepositoryServiceExtra service = UtilGH.repositoryService();

		final Repository repository = service.getRepository("barchart",
				"barchart-http");

		final List<RepositoryHookExtra> hookList = service
				.getHooksExtra(repository);

		for (final RepositoryHookExtra hook : hookList) {
			log.info("hook \n{}", hook);
		}

	}

}
