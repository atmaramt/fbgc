package org.albino.xmpp.listener;

import org.albino.xmpp.SessionHandler;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

public class MessageListener implements org.jivesoftware.smack.MessageListener {
	Logger logger = Logger.getLogger(MessageListener.class);
	
	SessionHandler sessionHandler;
	
	public MessageListener(SessionHandler sessionHandler) {
		this.sessionHandler = sessionHandler;
	}
	
	public void processMessage(Chat chat, Message message) {
		if(message.getBody()==null){
			logger.debug(message.getFrom() + " is typing...");
			return;
		}
			
		logger.debug("Message received: " + message.getBody() + ", type: "
					+ message.getType());
		
		sessionHandler.sendMessage(chat.getParticipant(), message.getBody());
	}
}
