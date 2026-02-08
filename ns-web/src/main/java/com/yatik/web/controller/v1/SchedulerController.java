package com.yatik.web.controller.v1;

import com.yatik.domain.service.NewsIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Scheduler", description = "Endpoints for scheduling and triggering news ingestion")
public class SchedulerController {

    private final NewsIngestionService ingestionService;

    @Value("${scheduler.internal-token}")
    private String internalServiceToken;

    @PostMapping("/refresh-news")
    @Operation(summary = "Trigger news ingestion", description = "Manually triggers the news ingestion process. Requires an internal token.")
    public ResponseEntity<Map<String, String>> scheduleNews(
            @RequestHeader("X-Internal-Token") String token
    ) {
        if (!internalServiceToken.equals(token)) {
            log.warn("Invalid internal token {}", token);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid internal token");
        }

        String taskId = UUID.randomUUID().toString();

        // Run async to prevent blocking the HTTP response
        CompletableFuture.runAsync(() -> {
            try {
                log.info("Starting scheduled ingestion task {}", taskId);
                ingestionService.fetchAndProcessNews("general");
            } catch (Exception e) {
                log.error("Task {} failed", taskId, e);
            }
        });

        return ResponseEntity.ok(Map.of(
                "message", "Task queued successfully",
                "taskId", taskId
        ));
    }
}
