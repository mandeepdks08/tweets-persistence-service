package com.tweets.consumer;

import java.time.LocalDateTime;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tweets.datamodel.Tweet;
import com.tweets.repository.TweetsRepository;
import com.tweets.util.AppConstants;
import com.tweets.util.GsonUtils;
import com.tweets.util.SystemContext;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TweetsConsumer {

	@Autowired
	private TweetsRepository tweetsRepo;

	@KafkaListener(topics = "tweets")
	public void listen(ConsumerRecord<String, String> record) {
		initCorrelationId(record);
		log.info("New record received {}", GsonUtils.getGson().toJson(record));
		Tweet tweet = GsonUtils.getGson().fromJson(record.value(), Tweet.class);
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

	private void initCorrelationId(ConsumerRecord<String, String> record) {
		String parentCorrelationId = new String(
				record.headers().lastHeader(AppConstants.PARENT_CORRELATION_ID_HEADER.getValue()).value());
		String childCorrelationId = new String(
				record.headers().lastHeader(AppConstants.CHILD_CORRELATION_ID_HEADER.getValue()).value());
		SystemContext.setParentCorrelationId(parentCorrelationId);
		SystemContext.setChildCorrelationId(childCorrelationId);
		MDC.put(AppConstants.MDC_PARENT_CORRELATION_ID_KEY.getValue(), parentCorrelationId);
		MDC.put(AppConstants.CHILD_CORRELATION_ID_HEADER.getValue(), childCorrelationId);
	}
}
