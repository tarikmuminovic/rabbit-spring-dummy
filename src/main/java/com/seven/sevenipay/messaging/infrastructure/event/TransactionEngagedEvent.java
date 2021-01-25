package com.seven.sevenipay.messaging.infrastructure.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransactionEngagedEvent(
    @JsonProperty("eventId") UUID eventId
//    @JsonProperty("payload") TransactionPayload payload
) {
}
