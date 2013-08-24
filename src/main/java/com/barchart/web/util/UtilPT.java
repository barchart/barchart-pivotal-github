package com.barchart.web.util;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.pivotal.client.PivotalClient;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.typesafe.config.Config;

/**
 * Pivotal rest-api and html-api utilities.
 */
public class UtilPT {

	private static final Logger log = LoggerFactory.getLogger(UtilPT.class);

	public static void ensurePivotalWebhook() throws IOException {

	}

	/**
	 * New authenticated REST client.
	 */
	public static PivotalClient clientRest() throws IOException {

		final Config reference = Util.reference();

		final String token = reference.getString("pivotal.token");

		final PivotalClient client = new PivotalClient();

		client.setToken(token);

		return client;

	}

	/**
	 * New authenticated HTML client.
	 */
	public static WebClient clientHtml() throws IOException {

		final Config reference = Util.reference();

		final String username = reference.getString("pivotal.username");
		final String password = reference.getString("pivotal.password");

		final String urlLogin = reference.getString("pivotal.page.login");

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

	}

	/**
	 * 
	 */
	public static void ensureWebhookAll() throws IOException {

		final Config reference = Util.reference();

		final WebClient client = clientHtml();

		final List<? extends Config> projectList = reference
				.getConfigList("project-list");

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
	 * 
	 */
	public static void ensureProject(final Config project) throws IOException {

		final String name = project.getString("name");

	}

	/**
	 * Configure observer call back.
	 */
	public static void ensureWebhook(final WebClient client,
			final String projectId) throws IOException {

		final Config reference = Util.reference();

		final String webhook = reference.getString("pivotal.webhook");
		final String urlIntergate = reference
				.getString("pivotal.page.integrate");

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

}
