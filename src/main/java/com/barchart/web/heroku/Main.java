package com.barchart.web.heroku;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.barchart.web.site.Github;
import com.barchart.web.site.Home;
import com.barchart.web.site.Pivotal;

/**
 * Jetty launcher.
 */
public class Main {

	public static void main(final String[] args) throws Exception {

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

		context.addServlet(new ServletHolder(new Home()), "/");
		context.addServlet(new ServletHolder(new Github()), "/github");
		context.addServlet(new ServletHolder(new Pivotal()), "/pivotal");

		server.start();

		server.join();

	}

}
