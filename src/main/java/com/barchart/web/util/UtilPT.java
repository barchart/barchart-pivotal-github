package com.barchart.web.util;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.pivotal.client.PivotalClient;
import com.barchart.pivotal.service.PivotalService;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.typesafe.config.Config;

/**
 * Pivotal rest-api and html-api utilities.
 */
public class UtilPT {

	/** project-id -> integration-name -> integration-id */
	private static ConcurrentMap<Integer, ConcurrentMap<String, Integer>> //
	projectIntegration = new ConcurrentHashMap<Integer, ConcurrentMap<String, Integer>>();

	/**
	 * Extract integration mapping.
	 */
	public static Integer integration(final Integer projectId,
			final String integrationName) {

		final ConcurrentMap<String, Integer> map = projectIntegration
				.get(projectId);

		if (map == null) {
			return null;
		}

		return map.get(integrationName);
	}

	/**
	 * Persist integration mapping.
	 */
	public static void integration(final Integer projectId,
			final String integrationName, final Integer integrationId) {

		ConcurrentMap<String, Integer> map = projectIntegration.get(projectId);

		if (map == null) {
			map = new ConcurrentHashMap<String, Integer>();
			projectIntegration.put(projectId, map);
		}

		map.put(integrationName, integrationId);

	}

	private static final ThreadLocal<WebClient> CLIENT_HTML = new ThreadLocal<WebClient>() {
		@Override
		protected WebClient initialValue() {
			try {
				final Config reference = Util.reference();

				final String username = reference.getString("pivotal.username");
				final String password = reference.getString("pivotal.password");

				final String urlLogin = reference
						.getString("pivotal.page.login");

				final WebClient client = new WebClient();
				client.getOptions().setJavaScriptEnabled(false);

				final HtmlPage login = client.getPage(urlLogin);

				log.info("login: " + login);

				final HtmlForm form = login
						.getFirstByXPath("//form[@action='/signin']");

				log.info("form: " + form);

				final HtmlTextInput textUser = form
						.getFirstByXPath("//input[@id='credentials_username']");
				final HtmlPasswordInput textPass = form
						.getFirstByXPath("//input[@id='credentials_password']");
				final HtmlSubmitInput button = form
						.getFirstByXPath("//input[@id='signin_button']");

				textUser.setValueAttribute(username);
				textPass.setValueAttribute(password);
				final HtmlPage dashboard = button.click();

				log.info("dashboard: " + dashboard);

				return client;
			} catch (final Throwable e) {
				log.error("CLIENT_HTML failure", e);
				return null;
			}
		}
	};

	private static final ThreadLocal<PivotalClient> CLIENT_REST = new ThreadLocal<PivotalClient>() {
		@Override
		protected PivotalClient initialValue() {
			try {
				final Config reference = Util.reference();

				final String token = reference.getString("pivotal.token");

				final PivotalClient client = new PivotalClient();

				client.setToken(token);

				return client;
			} catch (final Throwable e) {
				log.error("CLIENT_REST failure", e);
				return null;
			}
		}
	};

	private static final Logger log = LoggerFactory.getLogger(UtilPT.class);

	/**
	 * New authenticated HTML client.
	 */
	public static WebClient clientHtml() throws IOException {
		return CLIENT_HTML.get();
	}

	/**
	 * New authenticated REST client.
	 */
	public static PivotalClient clientRest() throws IOException {
		return CLIENT_REST.get();
	}

	public static void ensurePivotalWebhook() throws IOException {

	}

	/**
	 * 
	 */
	public static void ensureProject(final Config project) throws IOException {

		final String name = project.getString("name");

	}

	/**
	 * 
	 */
	public static void ensureProjectAll() throws IOException {

		final Config reference = Util.reference();

		final List<? extends Config> projectList = reference
				.getConfigList("project-list");

		for (final Config project : projectList) {
			ensureProject(project);
		}

		final String json = "";

	}

	/**
	 * Configure observer call back.
	 */
	public static void ensureWebhook(final WebClient client, final int projectId)
			throws IOException {

		final Config reference = Util.reference();

		final String webhook = reference.getString("pivotal.webhook");
		final String urlIntergate = reference.getString(
				"pivotal.page.integrate").replace("(project_id)",
				Integer.toString(projectId));

		final HtmlPage integrate = client.getPage(urlIntergate);

		log.info("integrate: " + integrate);

		final HtmlForm form = integrate
				.getFirstByXPath("//div[@class='webhooks']//form");

		log.info("form: " + form);

		final HtmlTextInput textURL = form
				.getFirstByXPath("//input[@id='activity_channel_webhook_url']");
		final HtmlSubmitInput button = form
				.getFirstByXPath("//input[@id='save_webhook_settings']");

		textURL.setValueAttribute(webhook);

		button.click();

	}

	/**
	 * 
	 */
	public static void ensureWebhookAll() throws IOException {

		final Config reference = Util.reference();

		final WebClient client = clientHtml();

		final List<? extends Config> projectList = reference
				.getConfigList("project-list");

		for (final Config project : projectList) {

			ensureWebhook(client, project.getInt("pivotal.id"));

		}

	}

	/**
	 * Configure pivotal -> github integration.
	 */
	public static void ensureIntegration(final WebClient client,
			final int projectId) throws IOException {

		final Config reference = Util.reference();

		final String integrationName = reference
				.getString("pivotal.integration.name");
		final String integrationBaseURL = reference
				.getString("pivotal.integration.base-url");
		final String integrationImportURL = reference
				.getString("pivotal.integration.import-url");

		final String urlIntergate = reference.getString(
				"pivotal.page.integrate").replace("(project_id)",
				Integer.toString(projectId));

		{

			// https://www.pivotaltracker.com/projects/896678/integrations/new?type=Other

			final String urlIntergateNew = urlIntergate + "/new?type=Other";

			final HtmlPage integrateNew = client.getPage(urlIntergateNew);

			final HtmlForm form = integrateNew
					.getFirstByXPath("//div[@class='integration']//form");

			log.info("form={}", form);

			final HtmlTextInput textName = form
					.getFirstByXPath("//input[@id='integration_name']");
			final HtmlTextInput textBaseURL = form
					.getFirstByXPath("//input[@id='integration_base_url']");
			final HtmlTextInput textImportURL = form
					.getFirstByXPath("//input[@id='integration_import_api_url']");
			final HtmlSubmitInput button = form
					.getFirstByXPath("//input[@id='create_button']");

			textName.setValueAttribute(integrationName);
			textBaseURL.setValueAttribute(integrationBaseURL);
			textImportURL.setValueAttribute(integrationImportURL);

			button.click();

		}

		{

			final HtmlPage integrate = client.getPage(urlIntergate);

			log.info("integrate: " + integrate);

			final HtmlTable table = integrate
					.getFirstByXPath("//table[@id='integrations_table']");

			log.info("table: " + table);

			final List<HtmlTableRow> rowList = table.getRows();

			Integer integrationId = null;

			for (final HtmlTableRow row : rowList) {
				final String id = row.getId();
				if (id == null || id.length() == 0) {
					continue;
				}
				final String name = row.getCell(2).getTextContent().trim();

				if (name.contains(integrationName)) {
					integrationId = Integer.parseInt(id.split("_")[1]);
				}
			}

			integration(projectId, integrationName, integrationId);

			log.info("integrationId={}",
					integration(projectId, integrationName));

		}

	}

	public static void ensureIntegrationAll() throws IOException {

		final Config reference = Util.reference();

		final WebClient client = clientHtml();

		final List<? extends Config> projectList = reference
				.getConfigList("project-list");

		for (final Config project : projectList) {

			ensureIntegration(client, project.getInt("pivotal.id"));

		}

	}

	/**
	 * 
	 */
	public static PivotalService pivotalService() throws IOException {
		return new PivotalService(clientRest());
	}

}
