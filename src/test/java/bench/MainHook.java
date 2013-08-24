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

		// https://github.com/github/github-services/blob/master/lib/services/kato.rb

		final Map<String, String> config = new HashMap<String, String>();
		config.put("webhook_url",
				"https://barchart-pivotal-github.herokuapp.com/github");

		final RepositoryHook request = new RepositoryHook();
		request.setActive(true);
		request.setName("lechat");
		request.setConfig(config);

		final RepositoryHook response = service.createHook(repository, request);

		final List<RepositoryHook> hookList = service.getHooks(repository);

		for (final RepositoryHook hook : hookList) {
			System.out.println("hook.name: " + hook.getName());
			System.out.println("hook.url : " + hook.getUrl());
			System.out.println("hook.conf: " + hook.getConfig());
		}

	}
}
