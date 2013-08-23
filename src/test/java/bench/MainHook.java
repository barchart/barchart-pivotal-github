package bench;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryHook;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

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
				"barchart", "barchart-pivotal-github");

		System.out.println("repository: " + service.getBranches(repository));

		final Map<String, String> config = new HashMap<String, String>();
		config.put("url",
				"https://barchart-pivotal-github.herokuapp.com/github");
		config.put("content_type", "json");
		config.put("ssl_version", "3");

		final RepositoryHook request = new RepositoryHook();
		request.setActive(true);
		request.setName("web");
		request.setConfig(config);

		final RepositoryHook response = service.createHook(repository, request);

		final List<RepositoryHook> hookList = service.getHooks(repository);

		for (final RepositoryHook hook : hookList) {
			System.out.println("hook: " + hook.getId());
			System.out.println("hook: " + hook.getName());
			System.out.println("hook: " + hook.getUrl());
		}

	}
}