package com.aksrua.chat.config;

import com.aksrua.chat.chat.ChatMessage;
import com.aksrua.chat.chat.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.message.SimpleMessageFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

	private final SimpMessagingTemplate messageTemplate;

	@EventListener
	public void handleWebSocketDisconnectListener(
			SessionDisconnectEvent event
	) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String username = (String) headerAccessor.getSessionAttributes().get("username");
		if (username != null) {
			log.info("user disconnected: {}", username);
			var chatMessage = ChatMessage.builder()
					.type(MessageType.LEAVE)
					.sender(username)
					.build();

			messageTemplate.convertAndSend("/topic/public", chatMessage);
		}
	}
}
