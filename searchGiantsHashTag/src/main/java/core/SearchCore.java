package core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import entity.TweetEntity;
import io.StoreTweet;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class SearchCore {
	private Properties prop;
	private Configuration configuration;
	private final String KEYPROPERTIES = "twitter4j.properties";
	private final StoreTweet store = StoreTweet.getInstance();

	public SearchCore() {
		prop = new Properties();
		try(FileInputStream is = new FileInputStream(KEYPROPERTIES)) {
			prop.load(is);
		}catch(FileNotFoundException fe) {
		}catch(IOException ioe){}
		configuration = new ConfigurationBuilder()
				.setOAuthConsumerKey(prop.getProperty("oauth.consumerKey"))
				.setOAuthConsumerSecret(prop.getProperty("oauth.consumerSecret"))
				.setOAuthAccessToken(prop.getProperty("oauth.accessToken"))
				.setOAuthAccessTokenSecret(prop.getProperty("oauth.accessTokenSecret"))
				.build();
	}
	public void search() {
		Twitter twitter = new TwitterFactory().getInstance();
        Query query = new Query();
        query.setQuery("#kyojin");

        QueryResult queryResult = null;
        try {
        	//検索を実行
			queryResult = twitter.search(query);
		} catch (TwitterException e1) {
			e1.printStackTrace();
		}

		System.out.println("ヒット数:" + queryResult.getTweets().size());

		List<String> result = new ArrayList<String>();

		for(Status tweet : queryResult.getTweets()){
			result.add(tweet.getText());
		}

		System.out.println(result);
	}

	public void streamingHash() {
        TwitterStream twStream = new TwitterStreamFactory(configuration).getInstance();
        twStream.addListener(new MyStatusListener());
        twStream.filter("#giants", "#TOKYOGIANTS", "#kyojin", "#巨人", "#ジャイアンツ");
	}

    class MyStatusListener implements StatusListener {
        public void onStatus(Status status) {
        	TweetEntity entity = new TweetEntity();
        	entity.setTimestamp(status.getCreatedAt().getTime());
        	entity.setScreenName(status.getUser().getScreenName());
        	entity.setTweet(status.getText());

        	store.save(entity);
        	System.out.println(entity);
        }

        public void onDeletionNotice(StatusDeletionNotice sdn) {
            System.out.println("onDeletionNotice.");
        }

        public void onTrackLimitationNotice(int i) {
            System.out.println("onTrackLimitationNotice.(" + i + ")");
        }

        public void onScrubGeo(long lat, long lng) {
            System.out.println("onScrubGeo.(" + lat + ", " + lng + ")");
        }

        public void onException(Exception excptn) {
            System.out.println("onException.");
        }

        @Override
        public void onStallWarning(StallWarning arg0) {
        }
    }

    public StoreTweet getStore() {
    	return store;
    }

}
