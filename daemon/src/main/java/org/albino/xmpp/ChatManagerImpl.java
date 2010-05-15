package org.albino.xmpp;

import java.util.HashMap;
import java.util.Map;

import org.albino.xmpp.listener.MessageListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;

public class ChatManagerImpl {
	MessageListener messageListener;
	
	public ChatManagerImpl(MessageListener messageListener) {
		this.messageListener = messageListener;
	}
	
	Map<String, Chat> chats = new HashMap<String, Chat>();
	ChatManager chatManager;
	
	public Chat getChat(String userId) {
		if (!chats.containsKey(userId)) {
			addChat(userId, chatManager.createChat(userId, messageListener));
		}

		return chats.get(userId);
	}
	
	public void addChat(String userId, Chat chat) {
		if (!chats.containsKey(userId)) {
			chats.put(userId, chat);
		}
	}
	
	public ChatManager getChatManager() {
		return chatManager;
	}
	
	public void setChatManager(ChatManager chatManager){
		this.chatManager = chatManager;
	}
}
