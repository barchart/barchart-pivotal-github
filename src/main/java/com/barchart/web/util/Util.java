package com.barchart.web.util;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;


import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Util {

	public static Config reference() {
		return ConfigFactory.defaultReference();
	}

	public static String consume(final HttpServletRequest request)
			throws IOException {

		final StringBuilder text = new StringBuilder();

		final BufferedReader reader = request.getReader();

		String line;
		while ((line = reader.readLine()) != null) {
			text.append(line).append('\n');
		}

		return text.toString();
	}

}
