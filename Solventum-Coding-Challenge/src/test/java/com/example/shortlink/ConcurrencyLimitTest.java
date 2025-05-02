package com.example.shortlink;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class ConcurrencyLimitTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests whether the API respects the configured concurrency limit (e.g., 5).
     * Submits 10 concurrent encode requests and expects:
     * - 5 to succeed with status 200
     * - 5 to be rejected with status 429 (Too Many Requests)
     */
    @Test
    public void testConcurrencyLimit() throws InterruptedException {
        int threadCount = 10; // Total number of concurrent threads to simulate
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Integer>> futures = new ArrayList<>();

        // Submit 10 concurrent POST requests to /api/encode
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            futures.add(executor.submit(() -> {
                String urlJson = "{\"url\": \"https://example.com/page/" + index + "\"}";
                try {
                    // Perform the POST request and return the HTTP status
                    return mockMvc.perform(post("/api/encode")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(urlJson))
                            .andReturn()
                            .getResponse()
                            .getStatus();
                } catch (Exception e) {
                    e.printStackTrace();
                    return -1; // return -1 in case of any exception
                }
            }));
        }

        // Wait for all threads to complete execution
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Count the number of successful (HTTP 200) responses
        long successCount = futures.stream().filter(f -> {
            try {
                return f.get() == 200;
            } catch (Exception e) {
                return false;
            }
        }).count();

        // Count the number of rate-limited (HTTP 429) responses
        long tooManyCount = futures.stream().filter(f -> {
            try {
                return f.get() == 429;
            } catch (Exception e) {
                return false;
            }
        }).count();

        // Assertions to validate concurrency behavior
        assertThat(successCount).isEqualTo(5); // should match concurrency.limit
        assertThat(tooManyCount).isEqualTo(5); // overflowed threads
    }
}
