package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

	@Value("${jwt.header}")
	public String header;

	@Value("${jwt.secret}")
	public String secret;

	@Value("${jwt.expireTime}")
	public long expireTime;

	@Value("${jwt.key}")
	public String key;

	public String createJwt(String id, String subject) {
		return Jwts.builder()
				.setId(id)
				.setSubject(subject)
				.setExpiration(new Date(System.currentTimeMillis() + expireTime))
				.signWith(SignatureAlgorithm.HS256, key)
				.compact();
	}

	public String createJwt(String id, String subject, Date date) {
		return Jwts.builder()
				.setId(id)
				.setSubject(subject)
				.setExpiration(date)
				.signWith(SignatureAlgorithm.HS256, key)
				.compact();
	}

	public Claims parseJwt(String jwt) {
		return Jwts.parser()
				.setSigningKey(key)
				.parseClaimsJws(jwt)
				.getBody();
	}

	public boolean isExpired(Claims claims) {
		if (claims == null)
			return false;
		return claims.getExpiration().before(new Date());//默认传入system currenttime
	}

	public String refreshToken(Claims claims) {
		if (claims == null)
			return null;
		return createJwt(claims.getId(), claims.getSubject());
	}

}
