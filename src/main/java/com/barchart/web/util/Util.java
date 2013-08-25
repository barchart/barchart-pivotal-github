package com.barchart.web.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Util {

	private static final Logger log = LoggerFactory.getLogger(Util.class);

	public static Config reference() {

		/** development-only file to emulate os/env */
		final String name = ".properties";
		final File file = new File(name);

		if (file.exists()) {
			log.info("properties: {}", file.getAbsolutePath());
			final Config props = ConfigFactory.parseFile(file);
			return ConfigFactory.parseResources("reference.conf")
					.withFallback(props).resolve();
		} else {
			return ConfigFactory.defaultReference();
		}

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

	public static boolean isModelField(final Field field) {
		final int modifiers = field.getModifiers();
		return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers);

	}

	public static List<String> modelFields(final Class<?> klass) {

		final List<String> list = new ArrayList<String>();

		final Field[] fieldArray = klass.getDeclaredFields();

		for (final Field field : fieldArray) {
			if (isModelField(field)) {
				list.add(field.getName());
			}
		}

		return list;

	}

}
