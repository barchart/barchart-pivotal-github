package bench;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.web.util.GooGl;
import com.gargoylesoftware.htmlunit.WebClient;

public class MainURL {

	private static final Logger log = LoggerFactory.getLogger(MainURL.class);

	public static void main(final String[] args) throws Exception {

		final WebClient client = new WebClient();
		client.getOptions().setJavaScriptEnabled(false);

		{
			// final String link =
			// "https://github.com/barchart/barchart-http/issues?milestone=1&state=open";
			// final String url = "http://tinyurl.com/api-create.php?url=" +
			// link;
			// final TextPage page = client.getPage(url);
			// log.info("page : {}", page.getContent());
		}

		{
			// final String link =
			// "https://www.pivotaltracker.com/s/projects/896678/epics/801662";
			// final String url = "http://tinyurl.com/api-create.php?url=" +
			// link;
			// final TextPage page = client.getPage(url);
			// log.info("page : {}", page.getContent());
		}

		{
			final String link = "https://www.pivotaltracker.com/s/projects/896678/epics/801662";
			log.info("shortURL : {}", GooGl.shortURL(link));
		}

		{
			final String link = "http://goo.gl/etOfEb";
			log.info("longURL : {}", GooGl.longURL(link));
		}

	}

}
