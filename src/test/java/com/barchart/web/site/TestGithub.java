package com.barchart.web.site;

import static org.junit.Assert.*;

import java.util.regex.Matcher;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestGithub {

	private static final Logger log = LoggerFactory.getLogger(TestGithub.class);

	@Test
	public void regex() throws Exception {

		final String url = "https://api.github.com/repos/octocat/Hello-World/issues/1347";

		final Matcher matcher = Github.RX_URL.matcher(url);

		matcher.find();

		assertEquals("octocat", matcher.group(1));
		assertEquals("Hello-World", matcher.group(2));

	}

}
