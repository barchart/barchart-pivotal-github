package bench;

import java.io.FileInputStream;
import java.util.Properties;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.pivotal.client.PivotalClient;
import com.barchart.pivotal.model.Epic;
import com.barchart.pivotal.model.Label;
import com.barchart.pivotal.model.Project;
import com.barchart.pivotal.model.Story;
import com.barchart.pivotal.service.PivotalService;
import com.barchart.web.util.UtilPT;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class MainPT {

	private static final Logger log = LoggerFactory.getLogger(MainPT.class);

	public static void main(final String[] args) throws Exception {

		final PivotalClient client = UtilPT.clientRest();

		final PivotalService service = new PivotalService(client);

		final Label label = new Label();
		label.name = "test : " + new DateTime().getMillis();
		label.project_id = 896678;

		log.info("labelCreate : {}", label);

		log.info("labelCreate : {}", service.labelCreate(label));

	}

	public static void mainEpicCreate(final String[] args) throws Exception {

		final PivotalClient client = UtilPT.clientRest();

		final PivotalService service = new PivotalService(client);

		final Epic epic = new Epic();
		epic.project_id = 896678;
		epic.name = "test : " + new DateTime();

		log.info("epicCreate : {}", epic);

		log.info("epicCreate : {}", service.epicCreate(epic));

	}

	public static void mainEpicList(final String[] args) throws Exception {

		final PivotalClient client = UtilPT.clientRest();

		final PivotalService service = new PivotalService(client);

		log.info("epicList : {}", service.epicList(896678));

	}

	public static void mainProjectCreate(final String[] args) throws Exception {

		final PivotalClient client = UtilPT.clientRest();

		final PivotalService service = new PivotalService(client);

		final Project project = new Project();
		project.name = "test : " + new DateTime();

		log.info("projectCreate : {}", project);

		log.info("projectCreate : {}", service.projectCreate(project));

	}

	public static void mainProjectList(final String[] args) throws Exception {

		final PivotalClient client = UtilPT.clientRest();

		final PivotalService service = new PivotalService(client);

		log.info("projectList : {}", service.projectList());

	}

	public static void mainStoryCreate(final String[] args) throws Exception {

		final PivotalClient client = UtilPT.clientRest();

		final PivotalService service = new PivotalService(client);

		final Story story = new Story();
		story.project_id = 896678;
		story.name = "test : " + new DateTime();
		story.story_type = "feature";

		log.info("storyCreate : {}", story);

		log.info("storyCreate : {}", service.storyCreate(story));

	}

	public static void mainStoryList(final String[] args) throws Exception {

		final PivotalClient client = UtilPT.clientRest();

		final PivotalService service = new PivotalService(client);

		log.info("storyList : {}", service.storyList(896678));

	}

	public static void mainWebhook(final String[] args) throws Exception {

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
