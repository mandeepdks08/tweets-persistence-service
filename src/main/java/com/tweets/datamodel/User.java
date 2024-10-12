package com.tweets.datamodel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class User extends DbBaseModel {
	private String userId;
	private String name;
	private String email;
	private String phone;
	private Boolean enabled;
	
	public static User createUserFromJsonObjet(JSONObject jsonObject) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
		User user = User.builder().build();
		user.setId(jsonObject.getLong("id"));
		user.setUserId(jsonObject.getString("userId"));
		user.setName(jsonObject.getString("name"));
		user.setEmail(jsonObject.getString("email"));
		user.setPhone(jsonObject.getString("phone"));
		user.setEnabled(jsonObject.getBoolean("enabled"));
		user.setCreatedOn(LocalDateTime.parse(jsonObject.getString("createdOn"), formatter));
		user.setProcessedOn(LocalDateTime.parse(jsonObject.getString("processedOn"), formatter));
		return user;
	}
}
