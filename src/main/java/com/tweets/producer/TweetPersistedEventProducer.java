package com.tweets.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.tweets.datamodel.Tweet;
import com.tweets.util.GsonUtils;

@Component
public class TweetPersistedEventProducer {

	private static final String TWEET_PERSISTED_TOPIC = "tweets.persisted";

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void publishEvent(Tweet tweet) {
		ProducerRecord<String, String> record = new ProducerRecord<>(TWEET_PERSISTED_TOPIC,
				GsonUtils.getGson().toJson(tweet));
		kafkaTemplate.send(record);
	}
}
