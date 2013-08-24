package bench;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryHook;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.web.util.RepositoryServiceExtra;
import com.barchart.web.util.UtilGH;

public class MainGH {

	private static final Logger log = LoggerFactory.getLogger(MainGH.class);

	public static void main(final String[] args) throws Exception {

		final Properties props = new Properties();
		props.load(new FileInputStream(".properties"));

		final String username = props.getProperty("GITHUB_USERNAME");
		final String password = props.getProperty("GITHUB_PASSWORD");
		final String secret = props.getProperty("GITHUB_SECRET");

		final GitHubClient client = new GitHubClient();
		client.setCredentials(username, password);

		final RepositoryServiceExtra service = new RepositoryServiceExtra(
				client);

		final IRepositoryIdProvider repository = service.getRepository(
				"barchart", "barchart-http");

		final String url = "https://barchart-pivotal-github.herokuapp.com/github";

		UtilGH.ensureGithubWebhook(service, repository, url, secret);

		final List<RepositoryHook> hookList = service.getHooks(repository);

		for (final RepositoryHook hook : hookList) {
			log.info("hook.name: " + hook.getName());
			log.info("hook.url : " + hook.getUrl());
			log.info("hook.conf: " + hook.getConfig());
		}

	}

}
