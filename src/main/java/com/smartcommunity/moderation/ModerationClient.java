package com.smartcommunity.moderation;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ModerationClient {
    private final RestClient restClient;
    private final String endpoint;

    public ModerationClient(RestClient.Builder builder,
                            @Value("${app.moderation.endpoint}") String endpoint) {
        this.restClient = builder.build();
        this.endpoint = endpoint;
    }

    public ModerationDecision check(String content) {
        try {
            JsonNode json = restClient.post()
                    .uri(endpoint)
                    .body(new ModerationRequest(content))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new IllegalStateException("Moderation API returned HTTP " + response.getStatusCode());
                    })
                    .body(JsonNode.class);

            if (json == null) {
                throw new IllegalStateException("Moderation API returned an empty response");
            }
            ModerationResponse response = new ModerationResponse(json);
            if (!response.hasRecognizedDecision()) {
                throw new IllegalStateException("FastAPI response schema is not recognized. Check /docs and update ModerationResponse.");
            }
            return new ModerationDecision(response.isFlagged(), response.reason());
        } catch (RestClientException | IllegalStateException ex) {
            throw new ModerationUnavailableException(
                    "The moderation service is unavailable or returned an unexpected response. Please try again.", ex);
        }
    }
}


