import entity.TweetEntity;
import io.StoreTweet;

public class StoreTest {
	public static void main(String[] args) {
		StoreTweet tweet = StoreTweet.getInstance();
		tweet.save(new TweetEntity(1L, "5chi", "hogehogehoge"));
	}
}
