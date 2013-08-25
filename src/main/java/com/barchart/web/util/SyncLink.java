package com.barchart.web.util;

/**
 * Sync link bean with service.
 */
public class SyncLink {

	/**
	 * Sync line prefix.
	 */
	public static final String SYNC = "SYNC";

	/**
	 * Github sync short url.
	 */
	public static final String GH = "GH=";

	/**
	 * Pivotal sync short url.
	 */
	public static final String PT = "PT=";

	/**
	 * Decode sync link from the text.
	 */
	public static SyncLink decode(final String text) {
		final String line = search(text);
		if (line == null) {
			return null;
		}
		final String[] termArray = line.split("\\s");
		String github = null;
		String pivotal = null;
		for (final String term : termArray) {
			if (term.startsWith(GH)) {
				github = term.replace(GH, "");
			}
			if (term.startsWith(PT)) {
				pivotal = term.replace(PT, "");
			}
		}
		if (github == null || pivotal == null) {
			return null;
		}
		final SyncLink sync = new SyncLink();
		sync.github = github;
		sync.pivotal = pivotal;
		return sync;
	}

	/**
	 * Encode as properties line;
	 */
	public static String encode(final SyncLink sync) {
		return SYNC + " " + GH + sync.github + " " + PT + sync.pivotal + "\n";
	}

	/**
	 * Find sync line in the text.
	 */
	public static String search(final String text) {
		if (text == null) {
			return null;
		}
		final String[] lineArray = text.split("\n");
		for (final String line : lineArray) {
			if (line.startsWith(SYNC) && line.contains(GH) && line.contains(PT)) {
				return line;
			}
		}
		return null;
	}

	/**
	 * Github short url.
	 */
	public String github;

	/**
	 * Pivotal short url.
	 */
	public String pivotal;

	/**
	 * Create empty sync link.
	 */
	public SyncLink() {
		//
	}

	/**
	 * Create sync link by parsing text.
	 */
	public SyncLink(final String text) {
		final SyncLink sync = decode(text);
		if (sync != null) {
			github = sync.github;
			pivotal = sync.pivotal;
		}
	}

	/**
	 * Sync link is valid when both {@link #github} and {@link #pivotal} are
	 * present.
	 */
	public boolean isValid() {
		return github != null && pivotal != null;
	}

	/**
	 * Render with {@link #encode(SyncLink)}.
	 */
	@Override
	public String toString() {
		return encode(this);
	}

}
