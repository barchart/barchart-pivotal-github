package com.barchart.web.heroku;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 */
public class ThreadFactory implements java.util.concurrent.ThreadFactory {

	private static final AtomicLong count = new AtomicLong(0);

	private final String prefix;

	public ThreadFactory(final String prefix) {
		this.prefix = prefix;
	}

	@Override
	public Thread newThread(final Runnable task) {
		final Thread thread = new Thread(task, prefix + "-"
				+ count.getAndIncrement());
		thread.setDaemon(true);
		return thread;
	}

}
