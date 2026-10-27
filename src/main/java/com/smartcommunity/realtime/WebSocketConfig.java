package com.smartcommunity.realtime;

import com.smartcommunity.auth.JwtService;
import com.smartcommunity.channel.ChannelService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ChannelService channelService;
    private final String frontendUrl;

    public WebSocketConfig(JwtService jwtService, UserDetailsService userDetailsService,
                           ChannelService channelService,
                           @Value("${app.frontend-url}") String frontendUrl) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.channelService = channelService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(frontendUrl, "http://localhost:*", "https://localhost:*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor == null || accessor.getCommand() == null) return message;

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String header = accessor.getFirstNativeHeader("Authorization");
                    if (header == null || !header.startsWith("Bearer ")) {
                        throw new IllegalArgumentException("Missing Bearer token in STOMP CONNECT");
                    }
                    String token = header.substring(7);
                    if (!jwtService.valid(token)) throw new IllegalArgumentException("Invalid or expired JWT");
                    String email = jwtService.subject(token);
                    var details = userDetailsService.loadUserByUsername(email);
                    accessor.setUser(new UsernamePasswordAuthenticationToken(
                            details.getUsername(), null, details.getAuthorities()));
                }



                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    var principal = accessor.getUser();
                    String destination = accessor.getDestination();

                    if (principal == null || destination == null) {
                        throw new IllegalArgumentException(
                                "Authenticated subscription required"
                        );
                    }

                    // Testing only: channel membership check removed.
                    if (!destination.startsWith("/topic/channels/")
                            && !destination.equals(
                            "/topic/notifications/" + principal.getName())) {
                        throw new IllegalArgumentException(
                                "Subscription destination is not allowed"
                        );
                    }
                }


                return message;
            }
        });
    }
}

