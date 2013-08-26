package com.barchart.pivotal.hook;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.pivotal.util.UtilGson;

public class TestEvent {

	private static final Logger log = LoggerFactory.getLogger(TestEvent.class);

	@Test
	public void commentCreate() throws Exception {

		final FileInputStream stream = new FileInputStream(
				"src/test/resources/pivotal-hook/comment-create.conf");
		final InputStreamReader reader = new InputStreamReader(stream, "UTF-8");

		final Event event = UtilGson.getGson().fromJson(reader, Event.class);

		log.info("event : {}", event);

	}

	@Test
	public void commentDelete() throws Exception {

		final FileInputStream stream = new FileInputStream(
				"src/test/resources/pivotal-hook/comment-delete.conf");
		final InputStreamReader reader = new InputStreamReader(stream, "UTF-8");

		final Event event = UtilGson.getGson().fromJson(reader, Event.class);

		log.info("event : {}", event);

	}

	@Test
	public void storyMove() throws Exception {

		final FileInputStream stream = new FileInputStream(
				"src/test/resources/pivotal-hook/story-move.conf");
		final InputStreamReader reader = new InputStreamReader(stream, "UTF-8");

		final Event event = UtilGson.getGson().fromJson(reader, Event.class);

		log.info("event : {}", event);

	}

	@Test
	public void storyUpdateDescription() throws Exception {

		final FileInputStream stream = new FileInputStream(
				"src/test/resources/pivotal-hook/story-update-description.conf");
		final InputStreamReader reader = new InputStreamReader(stream, "UTF-8");

		final Event event = UtilGson.getGson().fromJson(reader, Event.class);

		log.info("event : {}", event);

	}

	@Test
	public void storyUpdateName() throws Exception {

		final FileInputStream stream = new FileInputStream(
				"src/test/resources/pivotal-hook/story-update-name.conf");
		final InputStreamReader reader = new InputStreamReader(stream, "UTF-8");

		final Event event = UtilGson.getGson().fromJson(reader, Event.class);

		log.info("event : {}", event);

	}

}
