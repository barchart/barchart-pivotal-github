package bench;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryHook;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import com.barchart.web.site.Init;

public class MainHook {

	public static void main(final String[] args) throws Exception {

		final Properties props = new Properties();
		props.load(new FileInputStream(".properties"));

		final String username = props.getProperty("GITHUB_USERNAME");
		final String password = props.getProperty("GITHUB_PASSWORD");

		final GitHubClient client = new GitHubClient();
		client.setCredentials(username, password);

		final RepositoryService service = new RepositoryService(client);

		final IRepositoryIdProvider repository = service.getRepository(
				"barchart", "barchart-http");

		System.out.println("repository: " + service.getBranches(repository));

		final String url = "https://barchart-pivotal-github.herokuapp.com/github";
		final RepositoryHook request = Init.githubWebhook(url);

		final RepositoryHook response = service.createHook(repository, request);

		final List<RepositoryHook> hookList = service.getHooks(repository);

		for (final RepositoryHook hook : hookList) {
			System.out.println("hook.name: " + hook.getName());
			System.out.println("hook.url : " + hook.getUrl());
			System.out.println("hook.conf: " + hook.getConfig());
		}

	}
}
