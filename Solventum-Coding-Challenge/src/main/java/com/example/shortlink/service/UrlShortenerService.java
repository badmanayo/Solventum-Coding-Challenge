package com.example.shortlink.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;




/**
 * Service class for handling URL encoding and decoding logic.
 * Stores mappings in an in-memory ConcurrentHashMap.
 */
@Service
public class UrlShortenerService {

	// In-memory store for shortUrl key -> original URL
	private final static Map<String, String> urlMap = new ConcurrentHashMap<>();
	
	
	// Base domain used for all shortened URLs
	private final String DOMAIN = "http://short.est/";
	
	
	
	/**
	 * Encodes the given original URL into a short URL.
	 * Stores the mapping in memory.
	 *
	 * @param originalUrl the original long URL
	 * @return the shortened URL
	 */

	public String encode(String originalUrl) {
		String key = generateKey(originalUrl);
		urlMap.put(key, originalUrl);
		return DOMAIN + key;
	}

	
	/**
	 * Decodes a short URL back to the original long URL.
	 *
	 * @param shortUrl the shortened URL
	 * @return the original long URL or null if not found
	 */
	public String decode(String shortUrl) {
		if (shortUrl == null || !shortUrl.startsWith(DOMAIN)) {
			System.out.println("Invalid or null shortUrl: " + shortUrl);
			return null;
		}

		// Extract the key from the short URL

		String key = shortUrl.replace(DOMAIN, "");
		String original = urlMap.get(key);

		if (original == null) {
			System.out.println("No match found in map for key: " + key);
		}

		return original;
	}

	/**
	 * Generates a 6-character deterministic key using UUID hash of the URL.
	 *
	 * @param url the original URL
	 * @return a shortened key
	 */
	
	private String generateKey(String url) {
		return UUID.nameUUIDFromBytes(url.getBytes()).toString().substring(0, 6);
	}
}
