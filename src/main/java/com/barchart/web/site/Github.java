package com.barchart.web.site;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Github webhook service.
 */
public class Github extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(Github.class);

	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		final PrintWriter writer = response.getWriter();

		writer.println("Github");

	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		final PrintWriter writer = response.getWriter();

		writer.println("Github");

		/**
		 * https://github.com/github/github-services/blob/master/lib/services/
		 * web.rb
		 */
		final String githubEvent = request.getHeader("X-GitHub-Event");
		final String githubDelivery = request.getHeader("X-GitHub-Delivery");

		log.info("githubEvent : {}", githubEvent);
		log.info("githubDelivery : {}", githubDelivery);

		final String payload = Util.consume(request);

		final Config config = ConfigFactory.parseString(payload);

		// config.getInt("number");

	}

}
