package org.albino.xmpp;

public interface SessionHandler {
	public void startSession(String sessionKey);
	public void stopSession();
	public void sendMessage(String messageContent);
	public void sendMessage(String id, String messageContent);
}
