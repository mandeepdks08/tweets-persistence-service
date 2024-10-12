package com.tweets.consumer;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tweets.datamodel.Tweet;
import com.tweets.repository.TweetsRepository;
import com.tweets.util.GsonUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TweetsConsumer {

	@Autowired
	private TweetsRepository tweetsRepo;

	@KafkaListener(topics = "tweets")
	public void listen(String message) {
		log.info("New message received {}", message);
		Tweet tweet = GsonUtils.getGson().fromJson(message, Tweet.class);
		if (tweet.getId() != null) {
			// Edit tweet
			String userId = tweet.getUserId();
			Tweet dbTweet = tweetsRepo.findById(tweet.getId()).orElse(null);
			if (dbTweet != null) {
				if (!dbTweet.getUserId().equals(userId)) {
					throw new RuntimeException(String.format("The tweet with id %s does not belong to the userid %s",
							String.valueOf(tweet.getId()), userId));
				} else {
					dbTweet.setTweet(tweet.getTweet());
					dbTweet.setProcessedOn(LocalDateTime.now());
					tweetsRepo.save(dbTweet);
					log.info("Tweet with id {} edited successfully", dbTweet.getId());
				}
			} else {
				throw new RuntimeException(
						String.format("The tweet with id %s does not exist", String.valueOf(tweet.getId())));
			}
		} else {
			// Save new tweet
			Tweet savedTweet = tweetsRepo.save(tweet);
			log.info("Tweet saved with id {}", savedTweet.getId());
		}
	}
}
