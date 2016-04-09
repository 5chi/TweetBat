package io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;

import entity.TweetEntity;

public class StoreTweet {
	private String userName;
	private String password;
	private String db      ;

	private String baseUrl;
	private final String KEYPROPERTIES = "cloudant.properties";
	private final ObjectMapper mapper = new ObjectMapper();
	private final DefaultHttpClient httpClient = new DefaultHttpClient();

	private static StoreTweet instance;

	public static synchronized StoreTweet getInstance() {
		if(instance == null) {
			instance = new StoreTweet();
		}
		return instance;
	}

	private StoreTweet() {
		Properties prop = new Properties();
		try(FileInputStream is = new FileInputStream(KEYPROPERTIES)) {
			prop.load(is);
			userName = prop.getProperty("cloudant.username");
			password = prop.getProperty("cloudant.password");
			db       = prop.getProperty("cloudant.db");

			baseUrl = "https://" + userName + ":" + password + "@" + userName + ".cloudant.com/" + db + "/";

		}catch(FileNotFoundException fe) {
		}catch(IOException ioe){}
	}

	public void save(TweetEntity entity) {
		String jsonDoc = "";
		try {
			jsonDoc = mapper.writeValueAsString(entity);
		}catch(Exception e) {return;}

		HttpPost httpPost = new HttpPost(baseUrl);
		httpPost.setEntity(new StringEntity(jsonDoc, ContentType.APPLICATION_JSON));
		addAuth(httpPost);

		try {
			// send the request and read the response
			HttpResponse postResp = httpClient.execute(httpPost);
			InputStream is = postResp.getEntity().getContent();
		  	ObjectNode postRespDoc = mapper.readValue(is, ObjectNode.class);
		  	String id = ((TextNode)postRespDoc.get("id")).getTextValue();
		  	System.out.println("The new document's ID is " + id + ".");
		} catch(IOException e) {
			System.out.println("An error occurred while creating a new document.");
			System.out.println(e.getMessage());
		} finally {
			// release the connection
		  	httpPost.releaseConnection();
		}
	}

	private void addAuth(HttpRequest req) {
		String encodedUserPass = new String(Base64.encodeBase64((userName + ":" + password).getBytes()));
		req.setHeader("Authorization", "Basic " + encodedUserPass);
	}

}
