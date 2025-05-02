package com.example.shortlink.controller;

import com.example.shortlink.dto.DecodeRequest;
import com.example.shortlink.dto.EncodeRequest;
import com.example.shortlink.service.UrlShortenerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.Semaphore;

@RestController
@RequestMapping("/api")
public class UrlShortenerController {

	private final UrlShortenerService shortenerService;
	private final Semaphore semaphore;
	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);


	@Autowired
	public UrlShortenerController(UrlShortenerService shortenerService, Semaphore semaphore) {
		this.shortenerService = shortenerService;
		this.semaphore = semaphore;
	}

	/**
	 * POST /api/encode
	 * Accepts a long URL and returns a shortened version.
	 * Limits concurrent requests using a semaphore.
	 */
	@PostMapping("/encode")
	public ResponseEntity<?> encode(@RequestBody EncodeRequest request) {

	
		// Reject request if concurrency limit is reached

		if (!semaphore.tryAcquire()) {
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
					.body(Map.of("error", "Too many concurrent requests"));
		}

		try {
			String shortUrl = shortenerService.encode(request.getUrl());
			return ResponseEntity.ok(Map.of("shortUrl", shortUrl));
		} finally {
			logger.info("Inside encode ");

			// Always release the permit after processing

			semaphore.release();
		}
		
	}
	
	
	/**
	 * POST /api/decode
	 * Accepts a short URL and returns the original long URL.
	 * Limits concurrent requests using a semaphore.
	 */

	@PostMapping("/decode")
	public ResponseEntity<?> decode(@RequestBody DecodeRequest request) {
		
		// Reject request if concurrency limit is reached

		if (!semaphore.tryAcquire()) {
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
					.body(Map.of("error", "Too many concurrent requests"));
		}

		try {
			String originalUrl = shortenerService.decode(request.getShortUrl());
			if (originalUrl == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Short URL not found"));
			}
			return ResponseEntity.ok(Map.of("originalUrl", originalUrl));
		} finally {
			logger.info("Inside decode ");

			// Always release the permit after processing

			semaphore.release();
		}
	}
}
