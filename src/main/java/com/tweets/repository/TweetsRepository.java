package com.tweets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tweets.datamodel.Tweet;

@Repository
public interface TweetsRepository extends JpaRepository<Tweet, Long> {

}
