package com.tweets.communicator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.tweets.datamodel.User;

@Component
public class UserServiceCommunicator {
	
	private static final String HOST = "http://localhost:8080";
	
	public List<User> getUsersDetails(List<String> userIds) {
		JSONObject request = new JSONObject();
		request.put("userIds", ObjectUtils.firstNonNull(userIds, new ArrayList<>()));
		JSONObject usersListResponse = new JSONObject(getResponse("/user/v1/list", request, RequestMethod.POST));
		JSONArray usersJsonArray = usersListResponse.getJSONArray("userIds");
		List<User> usersDetails = usersJsonArray.toList().stream().map(obj -> (User) obj).collect(Collectors.toList());
		return ObjectUtils.firstNonNull(usersDetails, new ArrayList<>());
	}
	
	public User authenticate(String token) throws Exception {
		JSONObject request = new JSONObject();
		request.put("token", token);
		JSONObject authenticationResponse = 
				getResponse("/user/v1/authenticate", request, RequestMethod.POST);
		if (authenticationResponse.getJSONObject("user") != null) {
			return User.createUserFromJsonObjet(authenticationResponse.getJSONObject("user"));
		} else {
			String error = (String) authenticationResponse.getJSONArray("errors").toList().get(0);
			throw new Exception("Failed to authenticate the user. Error message is: " + error);
		}
	}
	
	private <T> JSONObject getResponse(String endpoint, T input, RequestMethod requestMethod) {
		RestTemplate restTemplate = new RestTemplate();
		if (requestMethod.equals(RequestMethod.POST)) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<>(input.toString(), headers);
			String response = restTemplate.postForObject(URI.create(HOST + endpoint), requestEntity, String.class);
			return new JSONObject(response);
		} else if (requestMethod.equals(RequestMethod.GET)) {
			String response = restTemplate.getForObject(URI.create(HOST + endpoint), String.class);
			return new JSONObject(response);
		}
		return null;
	}
}