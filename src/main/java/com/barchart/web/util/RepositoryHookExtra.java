package com.barchart.web.util;

import org.eclipse.egit.github.core.RepositoryHook;

/**
 * Extend to reflect new Github API.
 */
public class RepositoryHookExtra extends RepositoryHook {

	private static final long serialVersionUID = 1L;

	private volatile String[] events = new String[0];

	public void setEvents(final String[] events) {
		this.events = events;
	}

	public String[] getEvents() {
		return events;
	}

	@Override
	public String toString() {
		final StringBuilder text = new StringBuilder();

		text.append("name=");
		text.append(getName());

		text.append("config=");
		text.append(getConfig());

		text.append("events=");
		text.append(getEvents());

		return text.toString();
	}

}
