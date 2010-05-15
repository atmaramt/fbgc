package org.albino.xmpp.listener;

import org.albino.xmpp.ChatManagerImpl;
import org.albino.xmpp.SessionHandler;
import org.jivesoftware.smack.Chat;

public class ChatManagerListener implements
		org.jivesoftware.smack.ChatManagerListener {

	SessionHandler sessionHandler;
	MessageListener messageListener;
	ChatManagerImpl chatManager;
	
	public ChatManagerListener(SessionHandler sessionHandler, MessageListener messageListener, ChatManagerImpl chatManager) {
		this.sessionHandler = sessionHandler;
		this.messageListener = messageListener;
		this.chatManager = chatManager;
	}
	
	public void chatCreated(Chat chat, boolean arg1) {
		chatManager.addChat(chat.getParticipant(), chat);

		chat.addMessageListener(messageListener);

	}//
}
