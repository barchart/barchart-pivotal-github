package com.barchart.web.heroku;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RunnableCore implements Runnable {

	private static final Logger log = LoggerFactory
			.getLogger(RunnableCore.class);

	private final String name;

	public RunnableCore(final String name) {
		this.name = name;
	}

	@Override
	public final void run() {
		try {
			runCore();
		} catch (final Throwable e) {
			log.error(name + " failure", e);
		}
	}

	protected abstract void runCore() throws Exception;

}
