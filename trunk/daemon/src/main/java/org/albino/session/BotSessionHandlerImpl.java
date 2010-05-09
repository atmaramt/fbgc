package org.albino.session;

import java.util.HashMap;
import java.util.Map;

import org.albino.DojoCommunicationHandler;

public class BotSessionHandlerImpl extends SessionHandlerImpl {

	public BotSessionHandlerImpl(String identifier,
			DojoCommunicationHandler handler) {
		super(identifier, handler);
	}
	
	public void handleIncomingMessage(String id, String message) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("action", "received");
		data.put("id", id);
		data.put("message", message);

		handler.processOutgoing(sessionIdentifier, data);
		
		
	}
}
