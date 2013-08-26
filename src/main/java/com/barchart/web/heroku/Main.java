package com.barchart.web.heroku;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.web.site.Github;
import com.barchart.web.site.Home;
import com.barchart.web.site.Pivotal;
import com.barchart.web.util.Util;
import com.barchart.web.util.UtilGH;
import com.barchart.web.util.UtilPT;
import com.barchart.web.util.UtilSyncIsstory;
import com.barchart.web.util.UtilSyncMilepic;
import com.typesafe.config.Config;

/**
 * Servlet container launcher.
 */
public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	private static final Config reference = Util.reference();

	private static final ScheduledExecutorService executor = Executors
			.newScheduledThreadPool(1, new ThreadFactory("main"));

	/** Periodic sync of milestone/epic */
	static final Runnable milepicTask = new RunnableCore("milepicTask") {
		@Override
		protected void runCore() throws Exception {
			UtilSyncMilepic.linkMilestoneEpicAll();
		}
	};

	/** Periodic sync of issue/story */
	static final Runnable isstoryTask = new RunnableCore("isstoryTask") {
		@Override
		protected void runCore() throws Exception {
			UtilSyncIsstory.linkIssueStoryAll();
		}
	};

	public static void main(final String[] args) throws Exception {

		UtilGH.ensureWebhookAll();

		UtilPT.ensureWebhookAll();
		UtilPT.ensureIntegrationAll();

		/** Periodic sync of milestone/epic */
		executor.scheduleAtFixedRate(milepicTask, 0,
				reference.getMilliseconds("project-sync.milepic-period"),
				TimeUnit.MILLISECONDS);

		/** Periodic sync of issue/story */
		executor.scheduleAtFixedRate(isstoryTask, 0,
				reference.getMilliseconds("project-sync.isstory-period"),
				TimeUnit.MILLISECONDS);

		//

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

		final Config path = reference.getConfig("heroku.app.path");

		context.addServlet(new ServletHolder(new Home()),
				path.getString("home"));
		context.addServlet(new ServletHolder(new Github()),
				path.getString("github"));
		context.addServlet(new ServletHolder(new Pivotal()),
				path.getString("pivotal"));

		server.start();

		server.join();

	}
}
