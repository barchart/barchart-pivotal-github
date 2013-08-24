package com.barchart.web.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.typesafe.config.Config;

public class UtilAWS {

	public static AmazonSNSClient clientAmazonSNS() {
	
		final Config config = Util.reference();
	
		final String access = config.getString("amazon.sns.access");
		final String secret = config.getString("amazon.sns.secret");
	
		final AWSCredentials creds = new BasicAWSCredentials(access, secret);
	
		final AmazonSNSClient client = new AmazonSNSClient(creds);
	
		return client;
	
	}

}
