package com.barchart.web.site;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Util {

	public static Config reference() {
		return ConfigFactory.defaultReference();
	}

	public static AmazonSNSClient amazonSNS() {

		final Config config = reference();

		final String access = config.getString("amazon.sns.access");
		final String secret = config.getString("amazon.sns.secret");

		final AWSCredentials creds = new BasicAWSCredentials(access, secret);

		final AmazonSNSClient client = new AmazonSNSClient(creds);

		return client;

	}

}
