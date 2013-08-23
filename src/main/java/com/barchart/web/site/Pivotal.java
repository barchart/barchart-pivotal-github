package com.barchart.web.site;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Home page.
 */
public class Pivotal extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(Pivotal.class);

	@Override
	protected void doGet(final HttpServletRequest requset,
			final HttpServletResponse response) throws ServletException,
			IOException {

		final PrintWriter writer = response.getWriter();

		writer.println("Pivotal");

	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		final PrintWriter writer = response.getWriter();

		writer.println("Pivotal");

	}

}
