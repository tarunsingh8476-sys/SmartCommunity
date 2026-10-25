package com.smartcommunity.realtime;

import java.util.Map;
import java.util.UUID;

public record ChannelMessageEvent(String type, UUID channelId, Map<String, Object> message) {
}
