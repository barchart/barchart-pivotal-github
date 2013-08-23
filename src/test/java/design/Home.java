package design;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest;
import com.amazonaws.services.sns.model.ConfirmSubscriptionResult;
import com.barchart.web.site.Util;

/**
 * Home page.
 */
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(Home.class);

	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse response) throws ServletException,
			IOException {

		final PrintWriter writer = response.getWriter();

		writer.println("Home");

	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		// Scan request into a string
		final Scanner scanner = new Scanner(request.getInputStream());
		final StringBuilder builder = new StringBuilder();
		while (scanner.hasNextLine()) {
			builder.append(scanner.nextLine());
		}

		// Parse the JSON message
		final InputStream stream = new ByteArrayInputStream(builder.toString()
				.getBytes());
		@SuppressWarnings("unchecked")
		final Map<String, String> message = new ObjectMapper().readValue(
				stream, Map.class);

		// Confirm the subscription
		if (message.get("Type").equals("SubscriptionConfirmation")) {

			final AmazonSNSClient client = Util.clientAmazonSNS();

			final String topicARN = message.get("TopicArn");
			final String tokenSNS = message.get("Token");

			final ConfirmSubscriptionRequest confirmRequest = new ConfirmSubscriptionRequest(
					topicARN, tokenSNS);

			final ConfirmSubscriptionResult result = client
					.confirmSubscription(confirmRequest);

			log.info("SubscriptionConfirmation : {}", result);
		}

	}

}
