package com.barchart.web.heroku;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.barchart.web.site.Github;
import com.barchart.web.site.Home;
import com.barchart.web.site.Pivotal;
import com.barchart.web.util.UtilGH;
import com.barchart.web.util.Util;
import com.typesafe.config.Config;

/**
 * Servlet container launcher.
 */
public class Main {

	public static void main(final String[] args) throws Exception {

		UtilGH.ensureGithubWebhookAll();

		/** Heroku provided. */
		final String PORT = System.getenv("PORT");

		final int port;
		if (PORT == null) {
			/** development */
			port = 8888;
		} else {
			/** heroku dynamic port */
			port = Integer.valueOf(PORT);
		}

		final Server server = new Server(port);

		final ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.NO_SESSIONS
						| ServletContextHandler.NO_SECURITY);

		context.setContextPath("/");

		server.setHandler(context);

		final Config reference = Util.reference();

		final Config pathConfig = reference
				.getConfig("heroku.application.path");

		context.addServlet(new ServletHolder(new Home()),
				pathConfig.getString("home"));
		context.addServlet(new ServletHolder(new Github()),
				pathConfig.getString("github"));
		context.addServlet(new ServletHolder(new Pivotal()),
				pathConfig.getString("pivotal"));

		server.start();

		server.join();

	}

}
