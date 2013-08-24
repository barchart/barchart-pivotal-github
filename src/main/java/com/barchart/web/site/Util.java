package com.barchart.web.site;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Util {

	public static Config reference() {
		return ConfigFactory.defaultReference();
	}

	public static AmazonSNSClient clientAmazonSNS() {

		final Config config = reference();

		final String access = config.getString("amazon.sns.access");
		final String secret = config.getString("amazon.sns.secret");

		final AWSCredentials creds = new BasicAWSCredentials(access, secret);

		final AmazonSNSClient client = new AmazonSNSClient(creds);

		return client;

	}

	public static GitHubClient clientGithubAPI() {

		final Config config = reference();

		final String username = config.getString("github.username");
		final String password = config.getString("github.password");

		final GitHubClient client = new GitHubClient();
		client.setCredentials(username, password);

		return client;

	}

	public static RepositoryService githubRepositoryService() {
		return new RepositoryService(clientGithubAPI());
	}

	public static String consume(final HttpServletRequest request)
			throws IOException {

		final StringBuilder text = new StringBuilder();

		final BufferedReader reader = request.getReader();

		String line;
		while ((line = reader.readLine()) != null) {
			text.append(line).append('\n');
		}

		return text.toString();
	}

}
