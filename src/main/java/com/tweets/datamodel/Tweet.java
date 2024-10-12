package com.tweets.datamodel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tweets")
@Getter
@Setter
@SuperBuilder
public class Tweet extends DbBaseModel {
	@Column(name = "userid")
	private String userId;
	@Column(name = "tweet")
	private String tweet;
	// In future we can add photos, videos, and audios as well
	
	@Override
	public String toString() {
		return "Tweet [userId=" + userId + ", tweet=" + tweet + ", id=" + id + ", createdOn=" + createdOn
				+ ", processedOn=" + processedOn + "]";
	}
}
