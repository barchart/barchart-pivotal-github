package bench;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class MainPT {

	private static final Logger log = LoggerFactory.getLogger(MainPT.class);

	// public static WebForm formWithAction(final WebForm[] formList,
	// final String action) {
	// for (final WebForm form : formList) {
	// if (action.equalsIgnoreCase(form.getAction())) {
	// return form;
	// }
	// }
	// return null;
	// }

	public static void main(final String[] args) throws Exception {

		final Properties props = new Properties();
		props.load(new FileInputStream(".properties"));

		final String username = props.getProperty("PIVOTAL_USERNAME");
		final String password = props.getProperty("PIVOTAL_PASSWORD");
		final String token = props.getProperty("PIVOTAL_TOKEN");

		final String hookURL = "https://barchart-pivotal-github.herokuapp.com/pivotal";

		final String urlLogin = "https://www.pivotaltracker.com/signin";

		final String urlIntergate = "https://www.pivotaltracker.com/projects/896918/integrations";

		final WebClient client = new WebClient();

		client.getOptions().setJavaScriptEnabled(false);

		{

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

		}

		{

			final HtmlPage integrate = client.getPage(urlIntergate);

			log.info("integrate: " + integrate);

			final HtmlForm form = integrate
					.getFirstByXPath("//div[@class='webhooks']//form");

			log.info("form: " + form);

			final HtmlTextInput textURL = form
					.getFirstByXPath("//input[@id='activity_channel_webhook_url']");
			final HtmlSubmitInput button = form
					.getFirstByXPath("//input[@id='save_webhook_settings']");

			textURL.setValueAttribute(hookURL);
			button.click();

		}

	}

}
