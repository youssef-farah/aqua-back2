package com.example.aqua.auth;

import lombok.Data;

@Data
public class GoogleTokenRequest {

	
	
	 private String token;
	    
	    public GoogleTokenRequest() {}
	    
	    public GoogleTokenRequest(String token) {
	        this.token = token;
	    }
}
