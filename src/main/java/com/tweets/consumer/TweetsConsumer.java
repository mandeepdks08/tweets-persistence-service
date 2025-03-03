package com.tweets.consumer;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tweets.datamodel.Tweet;
import com.tweets.producer.TweetPersistedEventProducer;
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

	@Autowired
	private TweetPersistedEventProducer tweetPersistedEventProducer;

	@KafkaListener(topics = "tweets.to-persist")
	public void listen(List<ConsumerRecord<String, String>> recordsList) {
		List<Tweet> tweetsToSave = new ArrayList<>();
		for (ConsumerRecord<String, String> record : recordsList) {
			try {
//				initCorrelationId(record);
				log.info("New record received {}", GsonUtils.getGson().toJson(record));
				Tweet tweet = GsonUtils.getGson().fromJson(record.value(), Tweet.class);
				tweetsToSave.add(tweet);
			} catch (Exception e) {
				log.error("Exception while saving tweet", e);
			}
		}
		List<Tweet> persistedTweets = tweetsRepo.saveAll(tweetsToSave);
		persistedTweets.forEach(persistedTweet -> tweetPersistedEventProducer.publishEvent(persistedTweet));
	}

//	private void initCorrelationId(ConsumerRecord<String, String> record) {
//		String parentCorrelationId = new String(
//				record.headers().lastHeader(AppConstants.PARENT_CORRELATION_ID_HEADER.getValue()).value());
//		String childCorrelationId = new String(
//				record.headers().lastHeader(AppConstants.CHILD_CORRELATION_ID_HEADER.getValue()).value());
//		SystemContext.setParentCorrelationId(parentCorrelationId);
//		SystemContext.setChildCorrelationId(childCorrelationId);
//		MDC.put(AppConstants.MDC_PARENT_CORRELATION_ID_KEY.getValue(), parentCorrelationId);
//		MDC.put(AppConstants.CHILD_CORRELATION_ID_HEADER.getValue(), childCorrelationId);
//	}
}
