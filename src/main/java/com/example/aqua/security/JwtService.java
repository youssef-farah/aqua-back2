package com.example.aqua.security;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.aqua.Useraqua.User;

import java.security.Key;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	  private static final String secretKey = "1563d639576351e33b7c17d630e3157e3cca776e820a36347a76a366a4b3d607";
	  private long jwtExpiration = 1000 * 60 * 60 * 24;
	  private long refreshExpiration = 1000 * 60 * 60 * 24 * 7;
	
	  public String extractUsername(String token) {
		    return extractClaim(token, Claims::getSubject);
		  }

		  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		    final Claims claims = extractAllClaims(token);
		    return claimsResolver.apply(claims);
		  }
		  
		    
		  private Claims extractAllClaims(String token) {
			    return Jwts
			        .parserBuilder()
			        .setSigningKey(getSignInKey())
			        .build()
			        .parseClaimsJws(token)
			        .getBody();
			  }

		private Key getSignInKey() {
			// TODO Auto-generated method stub
			byte[] keyBytes = secretKey.getBytes();
			    return Keys.hmacShaKeyFor(keyBytes);
		}
		
		  public String generateToken(
			      Map<String, Object> extraClaims,
			      UserDetails userDetails
			  ) {
			    return buildToken(extraClaims, userDetails, jwtExpiration);
			  }
		
		  public String generateToken(UserDetails userDetails) {
			    return generateToken(new HashMap<>(), userDetails);
			  }
		  
		  
		  public boolean isTokenValid(String token, UserDetails userDetails) {
			    final String username = extractUsername(token);
			    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
			  }

			  private boolean isTokenExpired(String token) {
			    return extractExpiration(token).before(new Date());
			  }
			  
			  private Date extractExpiration(String token) {
				    return extractClaim(token, Claims::getExpiration);
				  }  
			  public String generateRefreshToken(
				      UserDetails userDetails
				  ) {
				    return buildToken(new HashMap<>(), userDetails, refreshExpiration);
				  }
			  
			  
			  
			  private String buildToken(
				        Map<String, Object> extraClaims,
				        UserDetails userDetails,
				        long expiration
				) {
				    // Add user ID
				    if (userDetails instanceof User) {
				        extraClaims.put("userId", ((User) userDetails).getId_user());
				    }

				    // Add roles into token claims
				    extraClaims.put("roles",
				            userDetails.getAuthorities().stream()
				                    .map(GrantedAuthority::getAuthority)
				                    .toList()
				    );

				    return Jwts.builder()
				            .setClaims(extraClaims)
				            .setSubject(userDetails.getUsername())
				            .setIssuedAt(new Date(System.currentTimeMillis()))
				            .setExpiration(new Date(System.currentTimeMillis() + expiration))
				            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
				            .compact();
				}

			  
			  public List<String> extractRoles(String token) {
				    Claims claims = extractAllClaims(token);
				    return claims.get("roles", List.class);
				}




	
}
