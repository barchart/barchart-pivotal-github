package com.barchart.github;

import org.eclipse.egit.github.core.RepositoryHook;

import com.barchart.pivotal.util.UtilGson;

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

	/**
	 * Render as JSON.
	 */
	@Override
	public String toString() {
		return UtilGson.getGson().toJson(this);
	}

}
